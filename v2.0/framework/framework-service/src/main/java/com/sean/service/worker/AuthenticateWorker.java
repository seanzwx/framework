package com.sean.service.worker;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.CacheConfiguration.TransactionalMode;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.commons.codec.digest.DigestUtils;

import com.sean.common.util.SecurityUtil;
import com.sean.service.core.Action;
import com.sean.service.core.FrameworkSpi;
import com.sean.service.core.Session;

/**
 * 认证工作节点
 * @author sean
 */
public class AuthenticateWorker implements Worker
{
	// 会话私钥缓存key=sid, val=encryptKey
	private static Cache encryptKeyCache;
	private CacheManager ehcacheManager;

	private FrameworkSpi frameworkSpi;

	private Worker nextWorker;

	public AuthenticateWorker(FrameworkSpi frameworkSpi, Worker nextWorker)
	{
		this.nextWorker = nextWorker;
		this.frameworkSpi = frameworkSpi;

		synchronized (frameworkSpi)
		{
			if (AuthenticateWorker.encryptKeyCache == null)
			{
				// 初始化缓存管理器
				Configuration ehcacheConfig = new Configuration();
				ehcacheConfig.setName("session cache");
				ehcacheConfig.setDynamicConfig(false);
				ehcacheConfig.setUpdateCheck(false);
				ehcacheConfig.setMonitoring("OFF");
				ehcacheManager = CacheManager.create(ehcacheConfig);

				CacheConfiguration cfg = new CacheConfiguration();
				cfg.name("EncryptKeyCache");
				cfg.maxEntriesLocalHeap(10000);
				cfg.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
				cfg.eternal(false);
				cfg.timeToIdleSeconds(3600);
				cfg.timeToLiveSeconds(3600);
				cfg.overflowToOffHeap(false);
				cfg.statistics(false);
				cfg.transactionalMode(TransactionalMode.OFF);

				encryptKeyCache = new Cache(cfg);
				ehcacheManager.addCache(encryptKeyCache);
			}
		}
	}

	@Override
	public void work(Session session, Action action) throws Exception
	{
		String sid = session.getSid();
		if (sid != null && !sid.isEmpty())
		{
			// 获取会话私钥
			Element element = encryptKeyCache.getQuiet(sid);
			String encryptKey = null;
			if (element == null)
			{
				encryptKey = frameworkSpi.getEncryptKey(sid);
				if (encryptKey != null)
				{
					element = new Element(sid, encryptKey);
					encryptKeyCache.putQuiet(element);
				}
			}
			else
			{
				encryptKey = element.getObjectValue().toString();
			}
			if (encryptKey != null)
			{
				// 开始摘要认证
				String sbf = session.getParameter("sbf");
				String seed = session.getParameter("seed");
				if (sbf != null && !sbf.isEmpty() && seed != null && !seed.isEmpty())
				{
					StringBuilder entry = new StringBuilder(sid.length() + seed.length() + encryptKey.length());
					// 验证公式：md5(sid + seed + entryKey)
					entry.append(sid).append(seed).append(encryptKey);
					String entryText = DigestUtils.md5Hex(entry.toString());
					if (entryText.equals(sbf))
					{
						// 摘要认证通过, 开始解密用户信息
						String userId = SecurityUtil.desDecrypt(sid, encryptKey.toString());
						if (userId != null)
						{
							session.setLoginInfo(Long.parseLong(userId));
							// 初始化用户上下文
							frameworkSpi.initUserContext(session.getUserId());
							// 继续下一个节点
							this.nextWorker.work(session, action);
							return;
						}
					}
				}
			}
		}
		session.denied();
	}
}
