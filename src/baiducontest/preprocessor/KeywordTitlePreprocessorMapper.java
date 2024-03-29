package baiducontest.preprocessor;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KeywordTitlePreprocessorMapper extends Mapper<Object, Text, Text, Text>{
	private static final String TYPE_SEPARATOR = "\t";
	private static final String FILE_TYPE = "2";
			
	  private final Text newKey = new Text();  
	  private final Text newVal = new Text();  
	  public void map(Object key, Text value, Context context)  
	       throws IOException, InterruptedException {  
	 	  String line = value.toString();  
	       int pos = line.indexOf("\t");
	
	       if (pos != -1) {          	 
	     	   newKey.set(line.substring(0, pos));
	    	   newVal.set(FILE_TYPE + TYPE_SEPARATOR + line.substring(pos + 1));	     	         
	           context.write(newKey, newVal);                            
	       }  
	   }  
}		

