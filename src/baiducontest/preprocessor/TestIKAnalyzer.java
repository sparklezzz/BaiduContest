package baiducontest.preprocessor;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import baiducontest.common.StringTuple;

public class TestIKAnalyzer {
	public static void main(String []args) throws IOException {
		final Analyzer analyzer = new IKAnalyzer(false);	//智能分词
		
		String str = "塘沽工商营业执照变更";
		
		TokenStream stream = analyzer.reusableTokenStream(str.toString(), new StringReader(str.toString()));
	    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	    StringTuple document = new StringTuple();
	    stream.reset();
	    while (stream.incrementToken()) {
	      if (termAtt.length() > 0) {
	        document.add(new String(termAtt.buffer(), 0, termAtt.length()));
	      }
	      System.out.println(termAtt.toString());
	    }
		
	}
}
