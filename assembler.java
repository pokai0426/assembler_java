import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
   
public class assembler{ 
        static String filename1,name,symbol,location,objectcode,opcode,oprand,Trecord="",TLOCCTRS="";
        static int LOCCTR=0,TLOCCTR,LOCCTRS=0,LOCCTREND=0,LOCCTRLEN=0,obcount=1,Tcount=0,TTcount=0;
        static String line;int i=1;	
		static String[] OPTAB = new String[] { "ADD", "AND", "COMP", "DIV", "J", "JEQ", 
	                                       "JGT", "JLT", "JSUB", "LDA", "LDCH", "LDL", 
										   "LDX", "MUL", "OR", "RD", "RSUB", "STA", "STCH", 
										   "STL", "STX", "SUB", "TD", "TIO", "TIX", "WD" }; 
	  static Hashtable <String, String> opTAB=new Hashtable <String, String> ();	
      static Hashtable <String, Integer> SYMTAB=new Hashtable <String, Integer> ();
   public static void main(String[] args)  throws IOException
   {    
       opTAB.put("ADD","18");
	   opTAB.put("AND","58");
	   opTAB.put("COMP","28");
	   opTAB.put("DIV","24");
	   opTAB.put("J","3C");
	   opTAB.put("JEQ","30");
	   opTAB.put("JGT","34");
	   opTAB.put("JLT","38");
	   opTAB.put("JSUB","48");
	   opTAB.put("LDA","00");
	   opTAB.put("LDCH","50");
	   opTAB.put("LDL","08");
	   opTAB.put("LDX","04");
	   opTAB.put("MUL","20");
	   opTAB.put("OR","44");
	   opTAB.put("RD","D8");
	   opTAB.put("RSUB","4C");
	   opTAB.put("STA","0C");
	   opTAB.put("STCH","54");
	   opTAB.put("STL","14");
	   opTAB.put("STX","10");
	   opTAB.put("SUB","1C");
	   opTAB.put("TD","E0");
	   opTAB.put("TIO","F8");
	   opTAB.put("TIX","2C");
	   opTAB.put("WD","DC");
	   
	  
       filename1=args[0];
	   
	    int noline=5;
		BufferedReader br=null;
        FileWriter fw = new FileWriter("intermediate.txt");
        FileWriter fw2 = new FileWriter("2.2.txt");
		FileWriter fw3 = new FileWriter("2.2(1).txt");
		FileWriter fw4 = new FileWriter("2.3.txt");
		/*PASS1*/
		try
		 {
             br=new BufferedReader(new InputStreamReader(new FileInputStream(filename1)));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("不存在 "+filename1+" 這個檔案");
            System.exit(0);
        }
       
	    while((line=br.readLine()) !=null)
		{
			StringTokenizer st=new StringTokenizer(line);
		   if(st.countTokens()==0)continue;
		   if(st.countTokens()==3)
		   {
			   name=st.nextToken();
			   symbol=name;
			   String op=st.nextToken();
			   if(SYMTAB.get(symbol)!=null)
			   {
				   System.out.printf("發生錯誤，有重複的SYMBOL:LINE %d  %s",noline,symbol);
				   System.exit(0);
				   
			   }
			   if(op.equals("START"))
			   {
				   location=st.nextToken();
				   LOCCTR=Integer.parseInt(location,16);
				   TLOCCTR=LOCCTR;
				   LOCCTRS=LOCCTR;
				  
			   }
			   
			   if(op.equals("WORD"))
			   {     TLOCCTR=LOCCTR;
				     LOCCTR=LOCCTR+3;
					 
			   }
			   if(op.equals("RESW"))
			   {
				    TLOCCTR=LOCCTR;
				   LOCCTR=LOCCTR+3;
			   }
			    if(op.equals("RESB"))
			   {
				   TLOCCTR=LOCCTR;
				   LOCCTR=LOCCTR+Integer.parseInt(st.nextToken());
			   }
			   if(op.equals("BYTE"))
			   {
				   TLOCCTR=LOCCTR;
				   String a=st.nextToken();
				   if(a.charAt(0)=='C')
				   {
					   LOCCTR=LOCCTR+a.length()-3;
				   } 
				   if(a.charAt(0)=='X')
				   {
					   a=a.replaceAll("'|X","");
					   LOCCTR=LOCCTR+(a.length()*4/8);
				   }
			   }
			   if(SearchOPTAB(op))
			  {  
		           TLOCCTR=LOCCTR;
				  LOCCTR=LOCCTR+3;
			  }
			 SYMTAB.put(symbol,TLOCCTR);  
		
		  } 
          if(st.countTokens()==2||st.countTokens()==1)	
		  {     
	          String op=st.nextToken();
			  if(SearchOPTAB(op))
			  {   
		          TLOCCTR=LOCCTR;
				  LOCCTR=LOCCTR+3;
			  }
			if(op.equals("END"))
			 {
				  LOCCTREND=LOCCTR;
				 /*System.out.println(noline+" "+line);*/
				 String s = noline+" "+line+"\n";
		          fw.write(s);
				 break;
			 }
		  }			  
		 
		    String Hex = Integer.toHexString(TLOCCTR);
			String s = noline+" "+Hex+" "+line+"\r\n";
		/*System.out.println(noline+" "+Hex+" "+line);*/
		 fw.write(s);
      		noline=noline+5;
		   		
}
     fw.flush();
        fw.close();
		br.close();
/*PASS2*/
 BufferedReader br2=new BufferedReader(new InputStreamReader(new FileInputStream("intermediate.txt")));
 fw2.write("LINE  LOC                  Source statement        Object code\r\n");
       while((line=br2.readLine()) !=null)
	   {
		     StringTokenizer st=new StringTokenizer(line);
			 if(st.countTokens()==5)
			 {
				 noline=Integer.parseInt(st.nextToken());
				 location=st.nextToken();
				 symbol=st.nextToken();
				 opcode=st.nextToken();
				 oprand=st.nextToken();
				 if(opcode.equals("START"))
				 {
					 objectcode="";
				 }
	              if(opcode.equals("RESW")||opcode.equals("RESB"))
				  {
					  objectcode="";
				  }
				  if(opcode.equals("WORD"))
				  {  
			          String oprandhex=Integer.toHexString(Integer.parseInt(oprand)); 
					  DecimalFormat v=new DecimalFormat("000000");
					  objectcode=v.format(Integer.parseInt(oprandhex));
				  }
				  if(opcode.equals("BYTE"))
				  {   objectcode="";
			           if(oprand.charAt(0)=='C')
					   {
					  for(int i=2;i<oprand.length()-1;i++)
					  {
						  int a=(int)oprand.charAt(i);
						  String ahex=Integer.toHexString(a); 
						  objectcode=objectcode+ahex;
					  }
					   }
					   if(oprand.charAt(0)=='X')
					   {
						   objectcode=oprand.substring(2,oprand.length()-1);
					   }
				  }
				  if(SearchOPTAB(opcode))
				  {

					 if(oprand.indexOf(",")==-1)
					 {
					 String Hex = Integer.toHexString(SYMTAB.get(oprand));
					  objectcode=opTAB.get(opcode)+Hex;
					 }
					 else
					 {
						 
					  String Hex = Integer.toHexString(SYMTAB.get(oprand.substring(0,oprand.length()-2))+32768);
					  objectcode=opTAB.get(opcode)+Hex;
					 }
				  }/*System.out.println(noline+" "+objectcode);*/
			 }
		   
		     if(st.countTokens()==4)
			 {
				 noline=Integer.parseInt(st.nextToken());
				 location=st.nextToken();
				 opcode=st.nextToken();
				 oprand=st.nextToken();
				 symbol="";
				 if(SearchOPTAB(opcode))
				 {  
			         if(oprand.indexOf(",")==-1)
					 { 
					 String Hex = Integer.toHexString(SYMTAB.get(oprand));
					  objectcode=opTAB.get(opcode)+Hex;
					 }
					 else
					 {
						 
					  String Hex = Integer.toHexString(SYMTAB.get(oprand.substring(0,oprand.length()-2))+32768);
					  objectcode=opTAB.get(opcode)+Hex;
					 }
				 }/*System.out.println(noline+" "+objectcode);*/
				 
			 }
		   if(st.countTokens()==3)
		   {   
	           noline=Integer.parseInt(st.nextToken());
			   String a=st.nextToken();
			   if(a.equals("END"))
			   {
				   opcode="END";
				   location="";
				   symbol="";
				   oprand=st.nextToken();
				   objectcode="";
				   
			   }
			   else
			   {   location=a;
		           opcode=st.nextToken();
				   symbol="";
				   oprand="";
		           
			      if(SearchOPTAB(opcode))
			       {
					    
				        objectcode=opTAB.get(opcode)+"0000";
			        }
				  else
				  {
					  objectcode="";
					  location="";
				  }
			   }
		   }
			if(st.countTokens()>5)
			{
				noline=Integer.parseInt(st.nextToken());
				location=st.nextToken();
				location="";
				opcode=st.nextToken();
				objectcode="";
				oprand="";
				symbol="";
				while(st.hasMoreTokens())
				{
					String a=st.nextToken();
					if(!a.equals("\n"))
					oprand=oprand+" "+a;
				}
				
			}
		   
			  /* System.out.println(noline+" "+objectcode);*/
		   
		/* System.out.printf("%-5d%-12s%-12s%-12s%-12s%-12s\r\n",noline,location.toUpperCase(),symbol,opcode,oprand,objectcode.toUpperCase());*/  
		   String s="%-5d%-12s%-12s%-12s%-12s%-12s\r\n";
		    fw2.write(String.format(s,noline,location.toUpperCase(),symbol,opcode,oprand,objectcode.toUpperCase()));
		   if(location.equals(""))
		   {
			   location="*";
		   }
		   if(symbol.equals(""))
		   {
			   symbol="*";
		   }
		   if(oprand.equals(""))
		   {
			   oprand="*";
		   }
		   if(objectcode.equals(""))
		   {
			   objectcode="*";
		   }
		   String s2="%-5d%-12s%-12s%-12s%-12s%-12s\r\n";
		   fw3.write(String.format(s2,noline,location.toUpperCase(),symbol,opcode,oprand,objectcode.toUpperCase()));
	   }
	   
	   fw2.flush();
        fw2.close();
        fw3.flush();
        fw3.close();
		br2.close();
/*2.3.txt*/
      BufferedReader br3=new BufferedReader(new InputStreamReader(new FileInputStream("2.2(1).txt")));
	  
	  while((line=br3.readLine()) !=null)
	  {   
		  
		  StringTokenizer st=new StringTokenizer(line);
		  noline=Integer.parseInt(st.nextToken());
		  location=st.nextToken();
	      symbol=st.nextToken();
	      opcode=st.nextToken();
		  oprand=st.nextToken();
		  objectcode=st.nextToken();
		  if(opcode.equals("START"))
		  {    
	          int l=LOCCTREND-LOCCTRS;
			  String s="H^%-6s^00%4s^%06X";
			  /*System.out.println(String.format(s,symbol,location,l));*/
			  fw4.write(String.format(s,symbol,location,l)+"\r\n");
		  }
		  else if(opcode.equals("END"))
		  {   
	         String s1="T^00%4s^%02X^%s";
			  /*System.out.println(String.format(s1,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1)));*/
			  fw4.write(String.format(s1,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1))+"\r\n");
			  String s="E^00%4X";
			 /* System.out.println(String.format(s,LOCCTRS));*/
			  fw4.write(String.format(s,LOCCTRS)+"\r\n");
		  }
      if(SearchOPTAB(opcode) || opcode.equals("WORD") ||opcode.equals("BYTE"))
	     {   if(TLOCCTRS.equals(""))TLOCCTRS=location;
		     Tcount=Tcount+(objectcode.length()*4/8);
             TTcount=TTcount+1;
			 Trecord=Trecord+objectcode+"^";
             if(TTcount==10)
             { 
		         String s="T^00%4s^%02X^%s";
				/* System.out.println(String.format(s,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1)));*/
				 fw4.write(String.format(s,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1))+"\r\n");
				 Tcount=0;TTcount=0;Trecord="";
				 TLOCCTRS="";
			 }				 
	     }
      else if(opcode.equals("RESW") ||opcode.equals("RESB"))
	   {   if(!Trecord.equals(""))
		   {
		   String s="T^00%4s^%02x^%s";
		   /*System.out.println(String.format(s,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1)));*/
		   fw4.write(String.format(s,TLOCCTRS,Tcount,Trecord.substring(0,Trecord.length()-1))+"\r\n");
		   Tcount=0;TTcount=0;Trecord="";
		   TLOCCTRS="";}
	   }
		  
	  }
	    br3.close();
	   fw4.flush();
        fw4.close();
		File file1 =new File("2.2(1).txt");
	    File file2 =new File("intermediate.txt");
		file1.delete();
		file2.delete();
		System.out.println("檔案已建立");
  }
  static boolean SearchOPTAB(String opcode)
	   {
		   for(int i=0;i<OPTAB.length;i++)
		   {
			   if(opcode.equals(OPTAB[i]))
				   return true;
		   }
		   return false;
	   }

}