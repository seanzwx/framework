package com.sean.ds.pool;

import org.apache.avro.ipc.NettyTransceiver;

/**
 * Avro RPC客户端
 * @author sean
 * @param <E>
 */
public class AvroClient<E>
{
	public NettyTransceiver transceiver;
	public E proxy;
}
