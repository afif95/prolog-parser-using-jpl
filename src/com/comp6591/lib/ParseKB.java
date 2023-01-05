package com.comp6591.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import com.comp6591.lib.ProgramParser;
import com.comp6591.lib.Clause;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ParseKB {
	
	static void combinationUtil(String arr[], String data[], int start,
	            int end, int index, int r, String name)
	{
		if (index == r)
		{
			System.out.print(name+"(");
			for (int j=0; j<r; j++) {
				if(j==r-1)
					System.out.print(data[j]+"");
				else
				System.out.print(data[j]+",");
			
			}
			System.out.print(")");
			System.out.println();
			System.out.print(name+"(");
			for(int j=r-1;j>=0;j--) {
				if(j==0)
					System.out.print(data[j]+"");
				else
				System.out.print(data[j]+",");
			}
			System.out.print(")");
			System.out.println("");
			
		return;
		}
		
		for (int i=start; i<=end && end-i+1 >= r-index; i++)
		{
		data[index] = arr[i];
		combinationUtil(arr, data, i+1, end, index+1, r,name);
		}
	}

	static void printCombination(String arr[], int n, int r, String name)
    {
        String data[]=new String[r];
 
        combinationUtil(arr, data, 0, n-1, 0, r, name);
    }
	
	static double f(double x) {
		return x * x * x * x - 3 * x * x - 3; 
	}

	static double g(double x) {
		return Math.pow(3 * x * x + 3, .25);
	}
	static void fixedpoint() {
		double p, p0, tol;
		int i = 1;
		int no;
		
		Scanner new1 = new Scanner(System.in);
		System.out.print("Enter approximate p: ");
		p0 = new1.nextDouble();

		System.out.print("Desired Tolerance: ");
		tol = new1.nextDouble();

		System.out.print("Maximum Iterations: ");
		no = new1.nextInt();


		while(i <= no) {
			p = g(p0);

			if(Math.abs(p - p0) < tol) {
				break;
			}
			System.out.printf("Iteration %d: Current value = %f\n", i, p);

			i++; 
			p0 = p;

			if(i > no) {
				System.out.print("Method Failed after " + no);
				System.out.print(" iterations");
			}
		}
	}
	
	public static void main(String[] args) {
		
		String dl_fp, db_fp;
		
		ArrayList<Clause> facts = new ArrayList<Clause>();
		ArrayList<Clause> rules = new ArrayList<Clause>();
		Scanner sc = new Scanner(System.in);
		System.out.println("DATALOG FILE PATH: ");
		dl_fp = sc.nextLine();
		File datalog = new File(dl_fp);
		InputStream in = null;
		
		ArrayList<String> idb = new ArrayList<String>();
		ArrayList<String> edb = new ArrayList<String>();
		Set<String> hu = new HashSet<String>();

		System.out.println("DATABASE FILE PATH: ");
		db_fp = sc.nextLine();
		File database = new File(db_fp);
		InputStream databasein1 = null;
		
		
		try {
			in = new FileInputStream(datalog);
			databasein1 = new FileInputStream(database);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			
			Arrays.stream(ProgramParser.parseKB(in)).forEach(clause -> {
				switch (clause.getType()) {
					case RULE:
					rules.add(clause);
					edb.add(clause.getHead().getName());
					Clause[] goals = clause.getGoals();
					for (int i = 0; i < goals.length; i++) {
						switch (goals[i].getType()) {
							case PROC:
								idb.add(goals[i].getName());
							break;
						}
					}
					break;
				}
			});

			System.out.print("EDB: ");
			
			Set<String> edbset = new HashSet<String>(edb);
			for (String temp : edbset){
	        	System.out.println(temp);
	        }
			
			System.out.print("IDB: ");
			
			Set<String> idbset = new HashSet<String>(idb);
			for (String temp : idbset){
	        	System.out.println(temp);
	        }
						
			
			Arrays.stream(ProgramParser.parseKB(databasein1)).forEach(clause -> {
				switch (clause.getType()) {
					case PROC:
					facts.add(clause);
					
					Clause[] arguments = clause.getArgs();
					for(int i=0; i<arguments.length;i++) {
						hu.add(arguments[i].toString());
					}
					
					
					break;
				}
			});
			
			System.out.print("HU (HERBRAND UNIVERSE): ");

			for (String temp : hu){
	        	System.out.println(temp);
	        }
			
			System.out.println("ADOM (ACTIVE DOMAIN): ");
			System.out.print("{");
			for(String adomtemp : hu) {
				System.out.print(adomtemp+" ");
			}
			System.out.print("}");
			System.out.println();
			
			System.out.print("HB (HERBRAND BASE): ");
			String[] array = hu.stream().toArray(n -> new String[n]);
			
			Iterator iterator = facts.iterator();
			Iterator iterator1 = rules.iterator();
			while(iterator.hasNext()) {
				Clause fact = (Clause) iterator.next();
				printCombination(array, hu.size(), fact.getArgs().length,fact.getName());
			}
//			while(iterator1.hasNext()) {
//				Clause rule = (Clause) iterator1.next();
//				Clause[] goals = rule.getGoals();
//				for (int i = 0; i < goals.length; i++) {
//					
//				printCombination(array, hu.size(), goals[i].getArgs().length,goals[i].getName());
//				
//				}
//			}
			
			
		} catch (Exception e) {
			
		}


		try {
			try {
				databasein1 = new FileInputStream(database);
				in = new FileInputStream(datalog);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			System.out.println("ENTER QUERY (TO EXIT TYPE 'QUIT'): ");
			Scanner sc1  = new Scanner(System.in);
			String query = sc1.nextLine();
			
			Clause clause;
			while(!query.equals("QUIT")) {
				clause= Clause.parse(query);
				Iterator iter;
				Iterator iter1;
				switch (clause.getType()) {
					case PROC:
						System.out.println(clause);
					try {
						iter = facts.iterator();
						Boolean flag = false;
						while(iter.hasNext()) {
							Clause fact = (Clause) iter.next();
							System.out.println("Tracer: "+fact);
							if (clause.getName().equals(fact.getName()) && Arrays.toString(clause.getArgs()).equals(Arrays.toString(fact.getArgs()))) {
								System.out.println("true.");
								flag = true;
								break;
							}
						}
						if (!flag) {
							iter = rules.iterator();
							while(iter.hasNext()) {
								Clause rule = (Clause) iter.next();
								System.out.println("Tracer: "+rule);
								if (rule.getHead().getName().equals(clause.getName()) && rule.getHead().getVariables().length == clause.getArgs().length) {
									String[] variables = rule.getHead().getVariables();
									

									Clause[] arguments = clause.getArgs();

									HashMap<String, Object> input = new HashMap<String, Object>();
									for (int i=0; i < clause.getArgs().length; i++) {
										System.out.println("Tracer: "+variables[i]);
										System.out.println("Tracer: "+arguments[i]);
										input.put(variables[i], arguments[i]);
									}
									Clause[] goals = rule.getGoals();
									Boolean goal_flag = true;
									for (int i = 0; i < goals.length; i++) {
										Clause goal = Clause.parse(goals[i].substitute(input));
										System.out.println("Tracer: "+goal);
										iter1 = facts.iterator();
										Boolean inner_flag = false;
										while(iter1.hasNext()) {
											Clause fact = (Clause) iter1.next();
											System.out.println("Tracer: "+fact);
											if (goal.getName().equals(fact.getName()) && Arrays.toString(goal.getArgs()).equals(Arrays.toString(fact.getArgs()))) {
												System.out.println(goal);
												inner_flag = true;
												break;
											}
										}
										if (!inner_flag) {
											goal_flag = false;
											break;
										}
									}
									if (goal_flag) {
										System.out.println("true.");
										flag = true;
										break;
									}
								}
							}
						}
						if (!flag) {
							System.out.println("false.");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					case RULE:
					break;
					default:
					System.out.println("???");
				}
				System.out.println("ENTER QUERY (TO EXIT TYPE 'QUIT'): ");
				query=sc1.nextLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
//		!!!code is only working for facts!!!!
//		code for non ground queries using fixed point iteration[root findings]
		
		
		
		
		try {
			try {
				databasein1 = new FileInputStream(database);
				in = new FileInputStream(datalog);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			System.out.println("ENTER NON GROUND QUERY (TO EXIT TYPE 'QUIT'): ");
			Scanner sc1  = new Scanner(System.in);
			String query = sc1.nextLine();
			Clause clause;
			while(!query.equals("QUIT")) {
				clause= Clause.parse(query);
				Iterator iter;
				Iterator iter1;
				switch (clause.getType()) {
					case PROC:
						System.out.println(clause);
					try {
						iter = facts.iterator();
						Boolean flag = false;
						while(iter.hasNext()) {
							Clause fact = (Clause) iter.next();
//							System.out.println("Tracer: "+fact);
							if (clause.getName().equals(fact.getName())) {
								String t = Arrays.toString(fact.getArgs());
								System.out.println(t);
								flag = true;
								break;
							}
						}
						if (!flag) {
							iter = rules.iterator();
							while(iter.hasNext()) {
								Clause rule = (Clause) iter.next();
//								System.out.println("Tracer: "+rule);
								if (rule.getHead().getName().equals(clause.getName()) && rule.getHead().getVariables().length == clause.getArgs().length) {
									String[] variables = rule.getHead().getVariables();
									Clause[] arguments = clause.getArgs();
									HashMap<String, Object> input = new HashMap<String, Object>();
									for (int i=0; i < clause.getArgs().length; i++) {
//										System.out.println("Tracer: "+variables[i]);
//										System.out.println("Tracer: "+arguments[i]);
										input.put(variables[i], arguments[i]);
									}
									Clause[] goals = rule.getGoals();
									Boolean goal_flag = true;
									for (int i = 0; i < goals.length; i++) {
										Clause goal = Clause.parse(goals[i].substitute(input));
//										System.out.println("Tracer: "+goal);
										iter1 = facts.iterator();
										Boolean inner_flag = false;
										while(iter1.hasNext()) {
											Clause fact = (Clause) iter1.next();
//											System.out.println("Tracer: "+fact);
											if (goal.getName().equals(fact.getName()) && Arrays.toString(goal.getArgs()).equals(Arrays.toString(fact.getArgs()))) {
//												System.out.println(goal);
												inner_flag = true;
												break;
											}
										}
										if (!inner_flag) {
											goal_flag = false;
											break;
										}
									}
									if (goal_flag) {
										System.out.println("true.");
										flag = true;
										break;
									}
								}
							}
						}
						if (!flag) {
							System.out.println("false.");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					case RULE:
					break;
					default:
					System.out.println("???");
				}
				System.out.println("ENTER NON GROUND QUERY (TO EXIT TYPE 'QUIT'): ");
				query=sc1.nextLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}