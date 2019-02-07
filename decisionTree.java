package ai_trabalho4;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

class Node{
	String path;
	String value;
	
	Node(String str){
		value = str;
		path = "";
	}
	
	Node(String str,String p){
		value = str;
		path = p;
	}
	
}

class Tree{
	
	Node root;
	List<Tree> subTrees;
	
 	Tree(List<String[]> st,int id){
		root = new Node(st.get(0)[id]," ");
		subTrees = new ArrayList<Tree>();
	}
	
	Tree(String st,String path){
		root = new Node(st,path);
		subTrees = new ArrayList<Tree>();	
	}
	
}

class ListColumnComparator implements Comparator<String[]>{
    private int column = 0;

    public ListColumnComparator(int c) {
        this.column = c;
    }
    
    public int compare(String[] a, String[] b) {
        double valA = Double.parseDouble(a[column]);
        double valB = Double.parseDouble(b[column]);
        
        if(valA > valB)
        	return 1;
        else if ( valA == valB)
        	return 0;
        else
        	return -1;
    }
}

class manageInputs{
	
	public static void printResults(String[] res) {
		
		System.out.println("\nThe obtained class results were:");
		System.out.println("[ Xi , Class ]");
		for(int i = 0; i < res.length ; i++) 
			System.out.println("[ X" + i + " , " + res[i] + " ]");
		
		
	}
	
	public static void printList(List<String[]> content) {
		for (Iterator<String[]> i = content.iterator(); i.hasNext();) {
			String[] item = i.next();
			System.out.print("[");
			for(int c=0; c < item.length; c++){
				System.out.print(item[c] + " , ");
		
			}
			System.out.println("]");
		}
	}
	
	public static void printList1(List<String> content) {
		for (Iterator<String> i = content.iterator(); i.hasNext();) {
			String item = i.next();
			System.out.println("["+item+"]");
		}
	}
	
	public static String[] deleteElement(String[] arr,int id) {
		String[] aux = new String[arr.length - 1];
		for (int c = 0 ; c < arr.length - 1;c++) {
			if(c >= id) {
				aux[c] = arr[c+1];			
			}else {	
				aux[c] = arr[c];			
			}
		}
		return aux;
	}
	
	public static List<String[]> readData(String file) { 
		List<String[]> content = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
		
			while ((line = br.readLine()) != null) {
				content.add(deleteElement(line.split(","),0));
			}
		
		} catch (IOException e) {
			System.out.println("Where's the file?");
		}
			
		
		/*for (Iterator<String[]> i = content.iterator(); i.hasNext();) {
			String[] item = i.next();
			System.out.print("[");
			for(int c=0; c < item.length; c++){
				System.out.print(item[c] + " , ");
		
			}
			System.out.println("]");
		}*/
		
		return content;
	}
	
	public static void checkForNumericalValue(List<String[]> content,int id){
		if(id > content.get(0).length - 1)
			return;
		
	    DecimalFormat decim = new DecimalFormat("00.00");
		
		try{
			double aux = Double.parseDouble(content.get(1)[id]);
			double sum = 0;
			for(int c = 1 ; c < content.size() ; c++){
			
				aux = Double.parseDouble(content.get(c)[id]);
				sum += aux;
			}
			double avg = sum/(content.size()-1);
			for(int c = 0 ; c < content.size() ; c++){
			
				aux = Double.parseDouble(content.get(c)[id]);
				if(aux < avg)
					content.get(c)[id] = "<" + decim.format(avg);
				else
					content.get(c)[id] = ">=" + decim.format(avg);
				sum++;
			}
			id++;
			checkForNumericalValue(content,id);
		}catch(NumberFormatException err){
			id++;
			checkForNumericalValue(content,id);
			
		}	
		
		
	}
	
	public static void checkForNumericalValueNonBinary(List<String[]> content,int id){
		if(id > content.get(0).length - 1)
			return;
		
	    DecimalFormat decim = new DecimalFormat("00.00");
		double aux;
		//ListColumnComparator comp = new ListColumnComparator(id); 
		//Collections.sort(content,comp);
		try{
			double max;String intervStr;
			ListColumnComparator comp = new ListColumnComparator(id); 
			Collections.sort(content,comp);
			aux = Double.parseDouble(content.get(0)[id]);
			double min = aux;
			int lastInterveralPosition=0;
			int interveralLength = 0;

			int goalPosition = content.get(0).length - 1;
			
			for(int i=0 ; i < content.size()-1 ; i++) {
				if(!(content.get(i)[goalPosition].equals(content.get(i+1)[goalPosition]))  && !(content.get(i)[id].equals(content.get(i+1)[id]))) {

					max = Double.parseDouble(content.get(i)[id]);
					
					if(interveralLength != 0 && i+1 != content.size()-1 && min != max) {

						
						intervStr = "[ " + decim.format(min) + " , " + decim.format(max) + " ]";
						
						for (int j = lastInterveralPosition; j <= i ; j++) 
							content.get(j)[id] = intervStr;
						
					
						lastInterveralPosition = i+1;
						
						min = Double.parseDouble(content.get(i+1)[id]);
						interveralLength = 0;
					}else {
						interveralLength++;
					}
				}else {
					interveralLength++;
				}
			}
			int pos = content.size()-1;
			max = Double.parseDouble(content.get(pos)[id]);
			
			intervStr = "[ " + decim.format(min) + " , " + decim.format(max) + " ]";
			
			for (int j = lastInterveralPosition; j <= pos ; j++) 
				content.get(j)[id] = intervStr;
			
			checkForNumericalValueNonBinary(content,id++);
		}catch(NumberFormatException err){

			id++;
			checkForNumericalValueNonBinary(content,id);
			
		}
	}
	
	public static void deleteColumn(List<String[]> table,int id) {
		int c=0;
		for (Iterator<String[]> i = table.iterator(); i.hasNext();) {
			String[] item = i.next();
			item = deleteElement(item, id);
			table.set(c, item);
			c++;
		}
	}

	public static boolean isInterveral(String val) {
		if (ID3.numericalHandling != 1)
			return false;
		String val1,val2;
		try {
			try {
				val1 = val.substring(2, 7);
				val2 = val.substring(10,15);
			}catch(StringIndexOutOfBoundsException err) {
				return false;
			}
			val1 = val1.replace(",",".");
			val2 = val2.replace(",",".");
			
			double aux = Double.parseDouble(val1);
			aux = Double.parseDouble(val2);
			return true;
		}catch(NumberFormatException err){
			return false;
		}
	}
	
	public static boolean isNumber(String str) {
		try {
			
			Double aux = Double.parseDouble(str);
			return true;
		}catch(NumberFormatException err){
			return false;
		}
		
	}

	public static int isCondition(String val) {
		if (ID3.numericalHandling != 2)
			return -1;
		
		String lessThan,greaterThan;
		try {
			try {
				greaterThan = val.substring(2);
				lessThan = val.substring(1);
			}catch(StringIndexOutOfBoundsException err) {
				return -1;
			}
			greaterThan = greaterThan.replace(",",".");
			lessThan = lessThan.replace(",",".");
			int correct = -1;
			
			try {
				double v = Double.parseDouble(greaterThan);
				correct++;
			}catch(NumberFormatException err) {
				
			}
			try {
				double v = Double.parseDouble(lessThan);
				correct++;
			}catch(NumberFormatException r1) {
				return correct;
			}
			return correct;
		}catch(NumberFormatException err){
			return -1;
		}
	}
	
}


class ID3{
	public static int numericalHandling;
	
	static Tree t;
	
	private static void tabThat(int tabs){
		for(int t11 = 0 ; t11 <= tabs ; t11++)
			System.out.print("  ");
	}
	
	public static boolean searchInList(List<String> strs,String val) {

		for (Iterator<String> i = strs.iterator(); i.hasNext();) {
			String item = i.next();
			if(item.equals(val))
				return true;
		}
		return false;
	}
	
	private static double log2(double n){
		return (Math.log(n) / Math.log(2));
	}
	
	public static int getMinEntropy(List<String[]> table){
		
		double min = getEntropyValue(table,0);
		double aux;
		int minVar = 0;
		for(int c = 1; c < table.get(0).length - 1 ; c++) {
			aux = getEntropyValue(table,c);
			//System.out.println("For atribute " + table.get(0)[c] + " entropy is " + aux);
			if(aux < min) {
				min = aux;
				minVar = c;
			}
		}
		
		return minVar;	
		
	}
	
	public static double getEntropyValue(List<String[]> table,int column){
		
		Map<String,Integer> atributeCount = new HashMap<String,Integer>();
		
		Map<String,Integer> occurences = new HashMap<String,Integer>();
		
		String key;
		String classKey;
		int aux;
		int entrys = 0;
		/*for(int i = 0 ; i < table.get(0).length ; i++)
			System.out.print(table.get(0)[i] + " | ");
		System.out.println();*/
		for (Iterator<String[]> i = table.iterator(); i.hasNext();) {
			String[] item = i.next();
			key = item[column];
			classKey = item[item.length-1];
			
			if(atributeCount.containsKey(key+classKey)){
				
				aux = atributeCount.get(key+classKey);
				aux++;
				atributeCount.put(key+classKey,aux);
			
			}else{
				
				atributeCount.put(key+classKey,1);
			}
			if(occurences.containsKey(key)){
				
				aux = occurences.get(key);
				aux++;
				occurences.put(key,aux);
			
			}else{
				
				occurences.put(key,1);
			}
			entrys++;
			
		
		}
		atributeCount.put("entrys",entrys);
		
		//System.out.println(" -- FIRST MAP -- ");
		
		return calculateEntropy(atributeCount,occurences);
		
	}	
	
	private static double calculateEntropy(Map<String,Integer> mp,Map<String,Integer> occ){
		int entrys = mp.get("entrys");
		double sum = 0;
		double s1 = 0;
		double aux;
		/*for (Map.Entry<String, Integer> entry : mp.entrySet()){
			
			
			System.out.println(" => ( " + entry.getKey() + " , " + entry.getValue() + " ) ");
			
		}
		for (Map.Entry<String, Integer> entry : occ.entrySet()){
			System.out.println(" => ( " + entry.getKey() + " , " + entry.getValue() + " ) ");
			
		}*/
		for (Map.Entry<String, Integer> key : occ.entrySet()){
			s1=0;
			for (Map.Entry<String, Integer> entry : mp.entrySet()){
				
				if( entry.getKey().startsWith(key.getKey(),0) ){
					
					aux = (double)entry.getValue() / key.getValue();
					
					s1 += -(aux * log2(aux)); 
				}	
				
			}
			sum +=  (((double)key.getValue()/entrys) * s1);
		}
		return sum;
		
	}
	
	public static boolean areAllTheSame(List<String[]> examples) {
		int pos = examples.get(0).length-1;
		for(int c=0;c < examples.size() - 1;c++) {
			if(!(examples.get(c)[pos].equals(examples.get(c+1)[pos])))
				return false;
		}
		return true;
	}
	
	public static String getClassification(List<String[]> examples) {
		int pos = examples.get(0).length-1;
		//manageInputs.printList(examples);
		//System.out.println();
		return examples.get(0)[pos];
	}
	
	public static String mostCommonAttribute(List<String[]> examples,int id) {

		Map<String,Integer> occurences = new HashMap<String,Integer>();
		int aux;
		String key;
		for(int c=0 ; c < examples.size() - 1;c++) {
			key = examples.get(c)[id];
			if(occurences.containsKey(key)){
				
				aux = occurences.get(key);
				aux++;
				occurences.put(key,aux);
			
			}else{
				
				occurences.put(key,1);
			}			
		}
		Map.Entry<String, Integer> maxEntry = null;

		for (Map.Entry<String, Integer> entry : occurences.entrySet())	{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
		        maxEntry = entry;
		    }
		}
		return maxEntry.getKey();
	}
	
	public static List<String> getValueFromAttribute(List<String[]> examples,int id) {
	
		List<String> lst = new ArrayList<String>();
		for(String[] item : examples) {
			if( !searchInList(lst,item[id]) )
				lst.add(item[id]);
		}
		
		return lst;
	}
		
	public static List<String[]> getSubSet(List<String[]> examples,String val,int id) {
		
		List<String[]> lst = new ArrayList<String[]>();
		for(String[] item : examples) {
			if(item[id].equals(val))
				lst.add(item);
		}
		
		return lst;
	}
	
	public static Tree decisionTreeLearning(List<String[]> examples,String[] attributes,List<String[]> par_examples,String targetVariable,List<String[]> originalSet,int tabs) {
		//Tree t1 = null;
		
		//if(tabs >= 20)
		//	return null;
		//manageInputs.printList(examples);
		//System.out.println();
		
		if(attributes.length == 1) {
			Tree res = new Tree(mostCommonAttribute(par_examples,par_examples.get(0).length - 1),targetVariable);
			//System.out.println("Most Common Attribute");
			//System.out.println("(" + res.root.value + "," + res.root.path + ")");
			System.out.println(res.root.value);
			return res;
		}else if ( areAllTheSame(examples) ) {
			Tree res = new Tree(getClassification(examples),targetVariable);
			System.out.println(res.root.value);
			
			return res;
		}else {
			int a = getMinEntropy(examples);
			
			tabs++;
			List<String> vals = getValueFromAttribute(originalSet,a);
			
			Tree t = new Tree(attributes[a],targetVariable);
			System.out.println();
			tabThat(tabs);	
			System.out.println("<" + attributes[a] + ">");
			tabs++;
			for(String vi : vals) {
				tabThat(tabs);
				System.out.print(vi + ": ");
				List<String[]> subSet_Examples = getSubSet(examples,vi,a);
				
				if(subSet_Examples.size() == 0) {
					Tree res = new Tree(mostCommonAttribute(examples,examples.get(0).length - 1),vi);
					t.subTrees.add(res);
					System.out.println(res.root.value);
					//System.out.println("(" + res.root.value + "," + res.root.path + ")");
				}else if(subSet_Examples.equals(examples)){
					Tree res = new Tree(mostCommonAttribute(examples,examples.get(0).length - 1),vi);
					System.out.println(res.root.value);
					return res;
				}else {
					
					//t1 = decisionTreeLearning(subSet_Examples,manageInputs.deleteElement(attributes, a),examples,vi);
					
					t.subTrees.add(decisionTreeLearning(subSet_Examples,attributes,examples,vi,originalSet,tabs));
					
					//System.out.println("Added subtree with root = " + t.root.value);
					
				}
			}
			return t;
		}
		
	}
	
	public static String[] getResults(List<String[]> examples,Tree original,int id) {
			
		Tree t1 = original;
		String varName,value="";
		int exCount = examples.size()-1,col;
		
		String[] results = new String[exCount];
		
		int x = 1;
	
		int i = 0;
		
		while(results[i] == null) {
			varName = t1.root.value;
			col = getVarColumn(varName,examples.get(0));
			
			value = examples.get(i+1)[col];
			
			for( Tree subT : t1.subTrees) {
				if (manageInputs.isInterveral(subT.root.path) && manageInputs.isNumber(value)) {
					String val1,val2;
					String str = subT.root.path;
					val1 = str.substring(2, 7);
					val2 = str.substring(10,15);
					
					val1 = val1.replace(",",".");
					val2 = val2.replace(",",".");
					double n1 = Double.parseDouble(val1);
					double n2 = Double.parseDouble(val2);
					double v = Double.parseDouble(value);
					if( v >= n1 && v <= n2 && subT.subTrees.size() == 0) {
						results[i] = subT.root.value;
						t1 = original;
						if( i + 1 < results.length) 
							i++;
						
						break;
							
					}else {
						t1 = subT;
					}
				}else if(numericalHandling == 2 && manageInputs.isNumber(value)) {
					x = manageInputs.isCondition(subT.root.path);
					
					if (x == 0) { //Path is greater than
						
						String greaterThan = subT.root.path.substring(2);
						greaterThan = greaterThan.replace(",",".");
						
						double v = Double.parseDouble(value);
						double n1 = Double.parseDouble(greaterThan);
						
						if( v >= n1) {
							if( subT.subTrees.size() == 0) {
								results[i] = subT.root.value;
								t1 = original;
								if( i + 1 < results.length) 
									i++;
								
								break;
									
							}else {
								t1 = subT;
							}
						}
					}else{	//Path is less than
						String lessThan = subT.root.path.substring(1);
						lessThan = lessThan.replace(",",".");
						
						double v = Double.parseDouble(value);
						double n1 = Double.parseDouble(lessThan);
						
						if( v < n1) {
							if( subT.subTrees.size() == 0) {
								results[i] = subT.root.value;
								t1 = original;
								if( i + 1 < results.length) 
									i++;
								
								break;
									
							}else {
								t1 = subT;
							}
						}
						
					}
				}else if ( value.equals(subT.root.path) ) {
					if( subT.subTrees.size() == 0) {
						results[i] = subT.root.value;
						t1 = original;
						if( i + 1 < results.length) 
							i++;
						
						break;
					}else {
						t1 = subT;
						//System.out.println("i = " + i + " and root = " + subT.root.value);
					}
				}			
			}
			
		}
		
		
		return results;
		
	}
	
	private static int getVarColumn(String varName,String[] arr) {
		for(int i = 0 ; i<arr.length;i++) {
			if(arr[i].equals(varName))
				return i;
		}
		
		return -1;
	}
	
}

public class decisionTree {
	
	public static void main (String[] args) {
		
		Scanner in = new Scanner(System.in);
		int op;
		
		System.out.println("Select csv file to load:");
		System.out.println("	1) restaurant.csv");
		System.out.println("	2) weather.csv");
		System.out.println("	3) iris.csv");
		System.out.print("\nOption: ");
		op = in.nextInt();
		
		List<String[]> data;
		
		switch(op) {
		case 1:
			data=manageInputs.readData("C:\\Users\\pedro\\eclipse-workspace\\IA_Trabalho04\\src\\ai_trabalho4\\restaurant.csv");
			break;
		case 2:
			data=manageInputs.readData("C:\\Users\\pedro\\eclipse-workspace\\IA_Trabalho04\\src\\ai_trabalho4\\weather.csv");
			break;
		case 3:
			data=manageInputs.readData("C:\\Users\\pedro\\eclipse-workspace\\IA_Trabalho04\\src\\ai_trabalho4\\iris.csv");
			break;
		default:
			System.out.print("Invalid option. We'll just load restaurant...");
			data=manageInputs.readData("C:\\Users\\pedro\\eclipse-workspace\\IA_Trabalho04\\src\\ai_trabalho4\\restaurant.csv");
			break;
		}
		String[] att = data.get(0);
		
		System.out.println("\nSelect numerical value handling method:");
		System.out.println("	1) Interveral handling");
		System.out.println("	2) Binary handling");
		System.out.println("	3) Do nothing");
		System.out.print("\nOption: ");
		op = in.nextInt();
		
		data.remove(0);
		switch(op) {
		case 1:
			manageInputs.checkForNumericalValueNonBinary(data,0);
			ID3.numericalHandling = 1;
			break;
		case 2:
			manageInputs.checkForNumericalValue(data,0);
			ID3.numericalHandling = 2;
			break;
		case 3:
			ID3.numericalHandling = 3;
			break;
		default:
			ID3.numericalHandling = 1;
			System.out.print("Invalid option. We'll just use the interverals...");
			manageInputs.checkForNumericalValueNonBinary(data,0);
			break;
		}
		
		
		System.out.println("\n	---Generated Tree---\n");
		Tree t1 = ID3.decisionTreeLearning(data, att, data,"",data,0);
		

		System.out.print("\nInput the name of the file with the test examples: ");
		String fileName = in.next();
		List<String[]> testExamples = manageInputs.readData("C:\\Users\\pedro\\eclipse-workspace\\IA_Trabalho04\\src\\ai_trabalho4\\" + fileName);
	//	manageInputs.printList(testExamples);
		String[] classRes = ID3.getResults(testExamples,t1,1);
		manageInputs.printResults(classRes);
		in.close();
	}
}