package com.sean.common.translate;

public class Main
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// System.out.println(Translator.translate("hello",
		// com.sean.framework.commom.translate.Language.ZH_CN));
//		String URL = "http://translate.google.cn/translate_a/t?client=t&text1={text}&hl=zh-CN&sl={source}&tl={target}&ie=UTF-8&oe=UTF-8&multires=1&prev=conf&psl=zh-CN&ptl=zh-CN&otf=1&it=sel.6041&ssel=0&tsel=0&sc=1";
//		System.out.println(Translator.translate(URL, "hello", "zh-CN", 0));
		
		System.out.println(Translator.translate("hello", "zh-CN"));
	}

}
