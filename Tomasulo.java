import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Tomasulo {
	static String instructionMemory[];
	static long dataMemory[];
	static String[] registerFile;
	static int pc;
	static int clock;
	static Object AddRS[][];
	static Object MulRS[][];
	static Object LB[][];
	static Object SB[][];
	static Object ExecutionTable[][];
	static Object CDB[];
	static boolean finished;
	static long A1;
	static long A2;
	static long A3;
	static long M1;
	static long M2;
	static boolean flagA1;
	static boolean flagA2;
	static boolean flagA3;
	static boolean flagM1;
	static boolean flagM2;
	static boolean flagS1;
	static boolean flagS2;
	static boolean flagS3;
	static boolean fullLB;
	static boolean fullSB;
	static boolean fullAddRS;
	static boolean fullMulRS;
	public Tomasulo() {

		instructionMemory = new String[1024]; // 2power10
		dataMemory = new long[2048];
		registerFile = new String[32];
		pc = 0;
		clock = 0;
		AddRS = new Object[4][8];
		MulRS = new Object[3][8];
		LB = new Object[4][4];
		SB = new Object[4][6];
		CDB = new Object[2];
		fullLB = false;
		fullSB = false;
		fullAddRS = false;
		fullMulRS = false;
		flagA1 = false;
		flagA2 = false;
		flagA3 = false;
		flagM1 = false;
		flagM2 = false;
		flagS1 = false;
		flagS2 = false;
		flagS3 = false;
		finished = false;
//initializing Reservation Stations
		for (int i = 1; i < 4; i++) {
			AddRS[i][0] = "A" + i;
			AddRS[i][1] = "" + 0;
			if (i != 3) {
				MulRS[i][0] = "M" + i;
				MulRS[i][1] = "" + 0;
			}
			LB[i][0] = "L" + i;
			LB[i][1] = "" + 0;
			SB[i][0] = "S" + i;
			SB[i][1] = "" + 0;
		}
//Labels
		AddRS[0][0] = " ";
		MulRS[0][0] = " ";
		LB[0][0] = " ";
		SB[0][0] = " ";
		AddRS[0][1] = "Busy";
		AddRS[0][2] = "opp ";
		AddRS[0][3] = "Vj ";
		AddRS[0][4] = "Vk ";
		AddRS[0][5] = "Qj ";
		AddRS[0][6] = "Qk ";
		AddRS[0][7] = "Time";
		MulRS[0][1] = "Busy";
		MulRS[0][2] = "opp ";
		MulRS[0][3] = "Vj ";
		MulRS[0][4] = "Vk ";
		MulRS[0][5] = "Qj ";
		MulRS[0][6] = "Qk ";
		MulRS[0][7] = "Time";
		LB[0][1] = "Busy";
		SB[0][1] = "Busy";
		LB[0][2] = "Address";
		SB[0][2] = "Address";
		SB[0][3] = "V ";
		SB[0][4] = "Q ";
		LB[0][3] = "Time";
		SB[0][5] = "Time";
// ExecutionTable[0][0]="Clock";

	}

//	public static void fillRS(String station, long value) {
////dont forget store
////MUL/DIV RS
//		for (int i1 = 1; i1 < MulRS.length; i1++) {
//			if (MulRS[i1][1] != "0") {
//				if (MulRS[i1][4] == null || MulRS[i1][6] == null) { // i have operands
//					if (MulRS[i1][3].toString().equals(station)) {
//						MulRS[i1][4] = value + "";
//						MulRS[i1][3] = null;
//					}
//					if (MulRS[i1][5].toString().equals(station)) {
//						MulRS[i1][6] = value + "";
//						MulRS[i1][5] = null;
//					}
//				}
//			}
//		}
////ADD/SUB RS
//		for (int i1 = 1; i1 < AddRS.length; i1++) {
//			if (AddRS[i1][1] != "0") {
//				if (AddRS[i1][4] == null || AddRS[i1][6] == null) { // i have operands
//					if (AddRS[i1][3].toString().equals(station)) {
//						AddRS[i1][4] = value + "";
//						AddRS[i1][3] = null;
//					}
//					if (AddRS[i1][5].toString().equals(station)) {
//						AddRS[i1][6] = value + "";
//						AddRS[i1][5] = null;
//					}
//				}
//			}
//		}
//	}

	public static void WriteBack() {
		// System.out.println("here");
		for (int i1 = 1; i1 < ExecutionTable.length; i1++) {
			long value = 0;
			String station;
			if (ExecutionTable[i1][7] != null && Integer.parseInt(ExecutionTable[i1][7].toString()) < clock
					&& ExecutionTable[i1][8] == null) {
				station = ExecutionTable[i1][4].toString();
				if (station.contains("L")) {
					int address = Integer.parseInt(ExecutionTable[i1][2].toString());
					//System.out.println("address" + address);
					value = dataMemory[address];
				} else if (station.contains("S")) {
					ExecutionTable[i1][8] = "" + clock;
					break;
				} else {
					switch (station) {
					case "A1":
						value = A1;
						break;
					case "A2":
						value = A2;
						break;
					case "A3":
						value = A3;
						break;
					case "M1":
						value = M1;
						break;
					case "M2":
						value = M2;
						break;
					default:
						break;
					}
					// System.out.println(ExecutionTable[i1][3]);
//					int operand1 = Integer
//							.parseInt(registerFile[Integer.parseInt(ExecutionTable[i1][2].toString().split("F")[1])]);
//					int operand2 = Integer
//							.parseInt(registerFile[Integer.parseInt(ExecutionTable[i1][3].toString().split("F")[1])]);
//					if (ExecutionTable[i1][0].toString().contains("MUL")) {
//						value = operand1 * operand2;
//					} else if (ExecutionTable[i1][0].toString().contains("DIV")) {
//						value = operand1 / operand2;
//					} else if (ExecutionTable[i1][0].toString().contains("ADD")) {
//						value = operand1 + operand2;
//					} else if (ExecutionTable[i1][0].toString().contains("SUB")) {
//						value = operand1 - operand2;
//					}
//					System.out.println("op1: " + operand1);
//					System.out.println("op2: " + operand2);
				}
				//System.out.println("res: " + value);
				// fillRS(ExecutionTable[i1][4].toString(), value); // bus role
				int registernum = Integer.parseInt(ExecutionTable[i1][1].toString().split("F")[1]);
				//System.out.println("registwerrr" + registernum);
				for (int k = 0; k < registerFile.length; k++) {
					if (k == registernum) {
						registerFile[k] = "" + value;
						break;
					}
				}
				for (int i = 1; i < AddRS.length; i++) {
					if (AddRS[i][1] != "0") {
						if (AddRS[i][3] == null) { // i have operands
							if (AddRS[i][5].toString().equals(station)) {

								if (AddRS[i][0].toString().equals("A1")) {
									flagA1 = true;
								} else if (AddRS[i][0].toString().equals("A2")) {
									flagA2 = true;
								} else if (AddRS[i][0].toString().equals("A3")) {
									flagA3 = true;
								}
								AddRS[i][3] = value + "";
								AddRS[i][5] = null;
							}
						}
						if (AddRS[i][4] == null) {
							if (AddRS[i][6].toString().equals(station)) {
								if (AddRS[i][0].toString().equals("A1")) {
									flagA1 = true;
								} else if (AddRS[i][0].toString().equals("A2")) {
									flagA2 = true;
								} else if (AddRS[i][0].toString().equals("A3")) {
									flagA3 = true;
								}
								AddRS[i][4] = value + "";
								AddRS[i][6] = null;
							}
						}

					}
				}
				for (int i = 1; i < MulRS.length; i++) {
					if (MulRS[i][1] != "0") {
						if (MulRS[i][3] == null) { // i have operands
							if (MulRS[i][5].toString().equals(station)) {
								if (MulRS[i][0].toString().equals("M1")) {
									flagM1 = true;
								} else if (MulRS[i][0].toString().equals("M2")) {
									flagM2 = true;
								}

								MulRS[i][3] = value + "";
								MulRS[i][5] = null;
							}
						}
						if (MulRS[i][4] == null) {
							if (MulRS[i][6].toString().equals(station)) {
								if (MulRS[i][0].toString().equals("M1")) {
									flagM1 = true;
								} else if (MulRS[i][0].toString().equals("M2")) {
									flagM2 = true;
								}
								MulRS[i][4] = value + "";
								MulRS[i][6] = null;
							}
						}

					}
				}
				for (int i = 1; i < SB.length; i++) {
					if (SB[i][1] != "0") {
						if (SB[i][3] == null) { // i have operands
							if (SB[i][4].toString().equals(station)) {
								if (SB[i][0].toString().equals("S1")) {
									flagS1 = true;
								} else if (SB[i][0].toString().equals("S2")) {
									flagS2 = true;
								} else if (SB[i][0].toString().equals("S3")) {
									flagS3 = true;
								}
								SB[i][3] = value + "";
								SB[i][4] = null;
							}
						}
					}
				}

				ExecutionTable[i1][8] = "" + clock;
				break;
			}
		}
// for (int i1=0;i1<LB.length;i1++) {
// if(LB[i1][1]!="0") {
// System.out.println(Integer.parseInt((String)LB[i1][3]));
// //dont forget t update that it is exec completed when ==0
// LB[i1][3]=(Integer.parseInt((String)LB[i1][3])-1)+""; //decrement the time
// if (Integer.parseInt((String)LB[i1][3])==-1) {
// //WB 
// LB[i1][1]=0+"";
// LB[i1][2]=null;
// LB[i1][3]=null;
// int address=Integer.parseInt(instructionMemory[i].split(",")[1]);
// long value=dataMemory[address]; //value to be written on the bus
// CDB[0]="L"+i1;
// CDB[1]=""+value;
// }
// }
// 
// }
	}

	public static void Execute(int addLatency, int subLatency, int mulLatency, int divLatency, int loadLatency,
			int storeLatency) {
//LD Buffer
//		System.out.println(flagA1);
//		System.out.println(flagM1);
		for (int i1 = 1; i1 < LB.length; i1++) {
			if (LB[i1][1] != "0") {
				if (Integer.parseInt(LB[i1][3].toString()) == loadLatency) {
					for (int j = 1; j < ExecutionTable.length; j++) {

						if (ExecutionTable[j][4] != null) {
							if (ExecutionTable[j][4].toString().equals(LB[i1][0].toString())) {
								ExecutionTable[j][6] = "" + clock; // execution start time
							}
						}
					}
				}
				
				
//System.out.println(Integer.parseInt((String)LB[i1][3]));
//dont forget t update that it is exec completed when ==0
					LB[i1][3] = (Integer.parseInt((String) LB[i1][3]) - 1) + ""; // decrement the time
					if ((Integer.parseInt((String) LB[i1][3])) == 0) {
						for (int j = 1; j < ExecutionTable.length; j++) {
							if (ExecutionTable[j][4] != null) {
								if (ExecutionTable[j][4].toString().equals(LB[i1][0].toString())) {
	//System.out.println("hanoun");
									ExecutionTable[j][7] = "" + clock; // execution end time
								}
							}
						}
					}
					if ((Integer.parseInt((String) LB[i1][3])) <= -1) {
						for (int j = 1; j < ExecutionTable.length; j++) {

								if (ExecutionTable[j][4]!=null &&ExecutionTable[j][4].toString().equals(LB[i1][0].toString())) {
									if(ExecutionTable[j][8]!=null) {
										LB[i1][1] = 0 + "";
										for (int k = 2; k <= 3; k++) {
											LB[i1][k] = null;
										}
										ExecutionTable[j][4]=null;
									}
								}
							
						}

					}
			}
		}
		for (int i1 = 1; i1 < SB.length; i1++) {
			if (SB[i1][1] != "0") {
				if (SB[i1][3] != null) {

					
					if (Integer.parseInt(SB[i1][5].toString()) == storeLatency) {
						if((SB[i1][0].toString().equals("S1") && !flagS1) ||(SB[i1][0].toString().equals("S2") && !flagS2) ||(SB[i1][0].toString().equals("S3") && !flagS3)) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(SB[i1][0].toString())) {
										
											ExecutionTable[j][6] = "" + clock;
										
										// execution start time
									}

								}
							}
						}else {
							SB[i1][5] = (Integer.parseInt((String) SB[i1][5]) +1) + "";
						}

						
					} 
						SB[i1][5] = (Integer.parseInt((String) SB[i1][5]) - 1) + ""; // decrement the time
						if ((Integer.parseInt((String) SB[i1][5])) == 0) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(SB[i1][0].toString())) {
	//System.out.println("hanoun");
										ExecutionTable[j][7] = "" + clock; // execution end time

									}
								}
							}
							dataMemory[Integer.parseInt((String) SB[i1][2])] = Long.parseLong((String) SB[i1][3]);
						}
						if ((Integer.parseInt((String) SB[i1][5])) <= -1) {
							for (int j = 1; j < ExecutionTable.length; j++) {

								if (ExecutionTable[j][4] != null&&ExecutionTable[j][4].toString().equals(SB[i1][0].toString())) {
									if(ExecutionTable[j][8]!=null) {
							SB[i1][1] = 0 + "";
							for (int k = 2; k <= 5; k++) {
								SB[i1][k] = null;
							}
							ExecutionTable[j][4]=null;
									}
								}
							}

						}
					
				}
			}
		}
//MUL RS
		for (int i1 = 1; i1 < MulRS.length; i1++) {
			if (MulRS[i1][1] != "0") {
				if (MulRS[i1][3] != null && MulRS[i1][4] != null&& MulRS[i1][2].toString().equals("MUL")) { // i have operands

					
					if ((Integer.parseInt((String) MulRS[i1][7])) == mulLatency) {
						// System.out.println("hanouna");
						if((MulRS[i1][0].toString().equals("M1") && !flagM1) ||(MulRS[i1][0].toString().equals("M2") && !flagM2)) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
										
											ExecutionTable[j][6] = "" + clock;
										
										// execution start time
									}

								}
							}
						}else {
							//System.out.println("h");
							MulRS[i1][7] = (Integer.parseInt((String) MulRS[i1][7]) +1) + "";
							
						}
					} 
						MulRS[i1][7] = (Integer.parseInt((String) MulRS[i1][7]) - 1) + ""; // decrement timer
						//System.out.println(MulRS[i1][7]);
						if ((Integer.parseInt((String) MulRS[i1][7])) == 0) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
										ExecutionTable[j][7] = "" + clock; // execution end time
									}
								}
							}
							Long value = Long.parseLong((String) MulRS[i1][3]) * Long.parseLong((String) MulRS[i1][4]);
							switch (MulRS[i1][0].toString()) {
							case "M1":
								M1 = value;
								break;
							case "M2":
								M2 = value;
								break;
							default:
								break;
							}
						}
						if ((Integer.parseInt((String) MulRS[i1][7])) <= -1) {
							for (int j = 1; j < ExecutionTable.length; j++) {

								if (ExecutionTable[j][4]!=null &&ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
									if(ExecutionTable[j][8]!=null) {
							MulRS[i1][1] = 0 + "";
							for (int k = 2; k <= 7; k++) {
								MulRS[i1][k] = null;
							}
							ExecutionTable[j][4]=null;
									}
								}
							}

						} 
					
				}
			}
		}
//div
		for (int i1 = 1; i1 < MulRS.length; i1++) {
			if (MulRS[i1][1] != "0") {
				if (MulRS[i1][3] != null && MulRS[i1][4] != null && MulRS[i1][2].toString().equals("DIV") ) { // i have operands

					
					if ((Integer.parseInt((String) MulRS[i1][7])) == divLatency) {
						if((MulRS[i1][0].toString().equals("M1") && !flagM1) ||(MulRS[i1][0].toString().equals("M2") && !flagM2)) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
										
											ExecutionTable[j][6] = "" + clock;
										
										// execution start time
									}

								}
							}
						}else {
							MulRS[i1][7] = (Integer.parseInt((String) MulRS[i1][7]) +1) + "";
						}
					}  
						MulRS[i1][7] = (Integer.parseInt((String) MulRS[i1][7]) - 1) + ""; // decrement timer
						if ((Integer.parseInt((String) MulRS[i1][7])) == 0) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
										ExecutionTable[j][7] = "" + clock; // execution end time
									}
								}
							}
							Long value = Long.parseLong((String) MulRS[i1][3]) / Long.parseLong((String) MulRS[i1][4]);
							switch (MulRS[i1][0].toString()) {
							case "M1":
								M1 = value;
								break;
							case "M2":
								M2 = value;
								break;
							default:
								break;
							}
						}
						if ((Integer.parseInt((String) MulRS[i1][7])) <= -1) {
							for (int j = 1; j < ExecutionTable.length; j++) {

								if (ExecutionTable[j][4]!=null &&ExecutionTable[j][4].toString().equals(MulRS[i1][0].toString())) {
									if(ExecutionTable[j][8]!=null) {
							MulRS[i1][1] = 0 + "";
							for (int k = 2; k <= 7; k++) {
								MulRS[i1][k] = null;
							}
							ExecutionTable[j][4]=null;
									}
								}
							}

						}
					
				}
			}
		}
//ADD RS
		for (int i1 = 1; i1 < AddRS.length; i1++) {
			if (AddRS[i1][1] != "0") {
				if (AddRS[i1][3] != null && AddRS[i1][4] != null && AddRS[i1][2].toString().equals("ADD")) { // i have operands

					
					if ((Integer.parseInt((String) AddRS[i1][7])) == addLatency) {
						if((AddRS[i1][0].toString().equals("A1") && !flagA1) ||(AddRS[i1][0].toString().equals("A2") && !flagA2)||(AddRS[i1][0].toString().equals("A3") && !flagA3)) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
										
											ExecutionTable[j][6] = "" + clock;
										
										// execution start time
									}

								}
							}
						}else {
							AddRS[i1][7] = (Integer.parseInt((String) AddRS[i1][7]) +1) + "";
						}
					}
						AddRS[i1][7] = (Integer.parseInt((String) AddRS[i1][7]) - 1) + ""; // decrement timer
						if ((Integer.parseInt((String) AddRS[i1][7])) == 0) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
										ExecutionTable[j][7] = "" + clock; // execution end time
									}
								}
							}
							Long value = Long.parseLong((String) AddRS[i1][3]) + Long.parseLong((String) AddRS[i1][4]);
							switch (AddRS[i1][0].toString()) {
							case "A1":
								A1 = value;
								break;
							case "A2":
								A2 = value;
								break;
							case "A3":
								A3 = value;
								break;
							default:
								break;
							}
						}
						if ((Integer.parseInt((String) AddRS[i1][7])) <= -1) {
							for (int j = 1; j < ExecutionTable.length; j++) {

								if (ExecutionTable[j][4]!=null &&ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
									if(ExecutionTable[j][8]!=null) {
							AddRS[i1][1] = 0 + "";
							for (int k = 2; k <= 7; k++) {
								AddRS[i1][k] = null;
							}
							ExecutionTable[j][4]=null;
									}
								}
							}

						}
					
				}
			}
		}
//sub
		for (int i1 = 1; i1 < AddRS.length; i1++) {
			if (AddRS[i1][1] != "0") {
				if (AddRS[i1][3] != null && AddRS[i1][4] != null && AddRS[i1][2].toString().equals("SUB")) { // i have operands
//System.out.println(AddRS[i1][7]);
					
					if ((Integer.parseInt((String) AddRS[i1][7])) == subLatency) {
						if((AddRS[i1][0].toString().equals("A1") && !flagA1) ||(AddRS[i1][0].toString().equals("A2") && !flagA2)||(AddRS[i1][0].toString().equals("A3") && !flagA3)) {
							for (int j = 1; j < ExecutionTable.length; j++) {
								if (ExecutionTable[j][4] != null) {
									if (ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
										
											ExecutionTable[j][6] = "" + clock;
										
										// execution start time
									}

								}
							}
						}else {
							AddRS[i1][7] = (Integer.parseInt((String) AddRS[i1][7]) +1) + "";
						}
					}  
						AddRS[i1][7] = (Integer.parseInt((String) AddRS[i1][7]) - 1) + ""; // decrement timer
					
					if ((Integer.parseInt((String) AddRS[i1][7])) == 0) {

						for (int j = 1; j < ExecutionTable.length; j++) {
							if (ExecutionTable[j][4] != null) {
								if (ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
									ExecutionTable[j][7] = "" + clock; // execution end time
								}
							}
						}
						Long value = Long.parseLong((String) AddRS[i1][3]) - Long.parseLong((String) AddRS[i1][4]);
						switch (AddRS[i1][0].toString()) {
						case "A1":
							A1 = value;
							break;
						case "A2":
							A2 = value;
							break;
						case "A3":
							A3 = value;
							break;
						default:
							break;
						}
					}
					if ((Integer.parseInt((String) AddRS[i1][7])) <= -1) {
						for (int j = 1; j < ExecutionTable.length; j++) {

							if (ExecutionTable[j][4]!=null &&ExecutionTable[j][4].toString().equals(AddRS[i1][0].toString())) {
								if(ExecutionTable[j][8]!=null) {
						AddRS[i1][1] = 0 + "";
						for (int k = 2; k <= 7; k++) {
							AddRS[i1][k] = null;
						}
						ExecutionTable[j][4]=null;
								}
							}
						}

					}
				}
			}
		}
	}

	public static boolean Issue(int addLatency, int subLatency, int mulLatency, int divLatency, int loadLatency,
			int storeLatency, int i, boolean issued) {
//Multiply

		if (instructionMemory[i].contains("MUL.D")&& !fullMulRS) {
			for (int j = 1; j < MulRS.length; j++) {
				if (MulRS[j][1] == "0") {
					issued = true;
					MulRS[j][1] = "" + 1; // busy
					MulRS[j][2] = "MUL"; // OP
					MulRS[j][7] = "" + (mulLatency); // Time
					int registernum1 = Integer.parseInt(instructionMemory[i].split(",")[1].split("F")[1]); // 1st
																											// operand
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum1) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								MulRS[j][3] = registerFile[k];
							} else {
								MulRS[j][5] = registerFile[k];
							}
							break;
						}
					}
					int registernum2 = Integer.parseInt(instructionMemory[i].split(",")[2].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum2) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								MulRS[j][4] = registerFile[k];
							} else {
								MulRS[j][6] = registerFile[k];
							}
							break;
						}
					}
//fill reg file
					int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum) {
							registerFile[k] = "M" + j;
							ExecutionTable[pc + 1][4] = "M" + j;
//System.out.println("reg"+registerFile[k]);
							break;
						}
					}
					ExecutionTable[pc + 1][5] = "" + clock;
					return true;
				}
			}
		}
//Divide
		else if (instructionMemory[i].contains("DIV")&& !fullMulRS) {
			for (int j = 1; j < MulRS.length; j++) {
				if (MulRS[j][1] == "0") {
					MulRS[j][1] = "" + 1;
					MulRS[j][2] = "DIV";
					MulRS[j][7] = "" + (divLatency);
					int registernum1 = Integer.parseInt(instructionMemory[i].split(",")[1].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum1) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								MulRS[j][3] = registerFile[k];
							} else {
								MulRS[j][5] = registerFile[k];
							}
							break;
						}
					}
					int registernum2 = Integer.parseInt(instructionMemory[i].split(",")[2].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum2) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								MulRS[j][4] = registerFile[k];
							} else {
								MulRS[j][6] = registerFile[k];
							}
							break;
						}
					}
//fill reg file
					int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum) {
							registerFile[k] = "M" + j;
							ExecutionTable[pc + 1][4] = "M" + j;
//System.out.println("reg"+registerFile[k]);
							break;
						}
					}
					ExecutionTable[pc + 1][5] = "" + clock;
					return true;
				}
			}
		} else if (instructionMemory[i].contains("ADD")&& !fullAddRS) {
			for (int j = 1; j < AddRS.length; j++) {
				if (AddRS[j][1] == "0") {
					AddRS[j][1] = "" + 1;
					AddRS[j][2] = "ADD";
					AddRS[j][7] = "" + (addLatency);
					int registernum1 = Integer.parseInt(instructionMemory[i].split(",")[1].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum1) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								AddRS[j][3] = registerFile[k];
							} else {
								AddRS[j][5] = registerFile[k];
							}
							break;
						}
					}
					int registernum2 = Integer.parseInt(instructionMemory[i].split(",")[2].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum2) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								AddRS[j][4] = registerFile[k];
							} else {
								AddRS[j][6] = registerFile[k];
							}
							break;
						}
					}
//fill reg file
					int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum) {
							registerFile[k] = "A" + j;
							ExecutionTable[pc + 1][4] = "A" + j;
//System.out.println("reg"+registerFile[k]);
							break;
						}
					}
					ExecutionTable[pc + 1][5] = "" + clock;
					return true;
				}
			}

		} else if (instructionMemory[i].contains("SUB" )&&!fullAddRS) {
			for (int j = 1; j < AddRS.length; j++) {
				if (AddRS[j][1] == "0") {
					AddRS[j][1] = "" + 1;
					AddRS[j][2] = "SUB";
					AddRS[j][7] = "" + (subLatency);
					int registernum1 = Integer.parseInt(instructionMemory[i].split(",")[1].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum1) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								AddRS[j][3] = registerFile[k];
							} else {
								AddRS[j][5] = registerFile[k];
							}
							break;
						}
					}
					int registernum2 = Integer.parseInt(instructionMemory[i].split(",")[2].split("F")[1]);
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum2) {
							if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
									&& !registerFile[k].contains("L")) {
								AddRS[j][4] = registerFile[k];
							} else {
								AddRS[j][6] = registerFile[k];
							}
							break;
						}
					}
//fill reg file
					int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum) {
							registerFile[k] = "A" + j;
							ExecutionTable[pc + 1][4] = "A" + j;
//System.out.println("reg"+registerFile[k]);
							break;
						}
					}
					ExecutionTable[pc + 1][5] = "" + clock;
					return true;
				}
			}
		} else if (instructionMemory[i].split(" ")[0].equals("L.D") && !fullLB) {
//System.out.println("laila");
			for (int j1 = 1; j1 < LB.length; j1++) {
				if (LB[j1][1] == "0") {
//Loading Load Buffer
					LB[j1][1] = "" + 1;
					LB[j1][2] = instructionMemory[i].split(",")[1];
					LB[j1][3] = "" + (loadLatency);
//System.out.println(instructionMemory[i].split(",")[1]); //address
//Updating Reg File
					int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
					for (int k = 0; k < registerFile.length; k++) {
						if (k == registernum) {
							registerFile[k] = "L" + j1;
							ExecutionTable[pc + 1][4] = "L" + j1;
//System.out.println("reg"+registerFile[k]);
							break;
						}
					}
					ExecutionTable[pc + 1][5] = "" + clock;
//System.out.println("clock "+clock);
					return true;
				}
			}
		}
//Store missing
		else if (instructionMemory[i].contains("S.D") && !fullSB) {
			//System.out.println("laila");
						for (int j1 = 1; j1 < SB.length; j1++) {
							if (SB[j1][1] == "0") {
			//Loading Load Buffer
								SB[j1][1] = "" + 1;
								SB[j1][2] = instructionMemory[i].split(",")[1];
								SB[j1][5] = "" + (storeLatency);
			//System.out.println(instructionMemory[i].split(",")[1]); //address
			//Updating Reg File
								int registernum = Integer.parseInt(instructionMemory[i].split(" ")[1].split(",")[0].split("F")[1]); // register
								for (int k = 0; k < registerFile.length; k++) {
									if (k == registernum) {
										if (!registerFile[k].contains("M") && !registerFile[k].contains("A")
												&& !registerFile[k].contains("L")) {
											SB[j1][3] = registerFile[k];
										} else {
											SB[j1][4] = registerFile[k];
										}
										break;
									}
								}
								ExecutionTable[pc + 1][4] = "S" + j1;
								ExecutionTable[pc + 1][5] = "" + clock;
			//System.out.println("clock "+clock);
								return true;
							}
						}
					}

		return false;

	}

	public static void readfile(int addLatency, int subLatency, int mulLatency, int divLatency, int loadLatency,
			int storeLatency) throws NumberFormatException {
		try {
//read instructions
			int in = 0;
			String s = "Program.txt";
			File file = new File(s); // creates a new file instance
			FileReader fr = new FileReader(file); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line); // appends line to string buffer
				sb.append("\n"); // line feed
				instructionMemory[in] = line;
				in++;
			}
			fr.close();
			ExecutionTable = new Object[in + 1][9];
			ExecutionTable[0][0] = "Instruction";
			ExecutionTable[0][1] = "Destination";
			ExecutionTable[0][2] = "Source1";
			ExecutionTable[0][3] = "Source2";
			ExecutionTable[0][4] = "Station";
			ExecutionTable[0][5] = "Issue";
			ExecutionTable[0][6] = "ExecutionStart";
			ExecutionTable[0][7] = "ExecutionEnd";
			ExecutionTable[0][8] = "Write Result";
			clock++;
			int x = 0;
			for (int a = 1; a < ExecutionTable.length; a++) {
				if (instructionMemory[x] != null) {
//System.out.println("inst"+instructionMemory[x]);
					ExecutionTable[a][0] = instructionMemory[x];
					ExecutionTable[a][1] = instructionMemory[x].split(" ")[1].split(",")[0]; // correct
					ExecutionTable[a][2] = instructionMemory[x].split(" ")[1].split(",")[1];
					if (instructionMemory[x].contains("MUL.D") || instructionMemory[x].contains("ADD")
							|| instructionMemory[x].contains("SUB") || instructionMemory[x].contains("DIV")) {
//System.out.println("inst"+instructionMemory[x].split(" ")[1].split(",")[2]);
						ExecutionTable[a][3] = instructionMemory[x].split(" ")[1].split(",")[2];
					} else {
//ExecutionTable[a][3]="";
					}
					x++;
				} else {
					break;
				}
			}
			while (!finished ) {
				fullLB=false;
				fullSB=false;
				fullAddRS=false;
				fullMulRS=false;
				int counter=0;
				for(int i=1;i<LB.length;i++) {
				
					if(LB[i][1]=="1")
						counter++;
				}
				if(counter==3)
					fullLB=true;
				counter=0;
				for(int i=1;i<SB.length;i++) {
					
					if(SB[i][1]=="1")
						counter++;
				}
				if(counter==3)
					fullSB=true;
				counter=0;
				for(int i=1;i<AddRS.length;i++) {
					if(AddRS[i][1]=="1")
						counter++;
				}
				if(counter==3)
					fullAddRS=true;
				counter=0;
				for(int i=1;i<MulRS.length;i++) {
					
					if(MulRS[i][1]=="1")
						counter++;
					//System.out.println(counter);
				}
				if(counter==2)
					fullMulRS=true;
				System.out.println("Clock Cycle: " + clock);
				System.out.println("PC: " + pc);
				boolean issued = false;
				WriteBack();
				// System.out.println("here2");
				Execute(addLatency, subLatency, mulLatency, divLatency, loadLatency, storeLatency);
				if (pc < instructionMemory.length && instructionMemory[pc] != null)
					issued = Issue(addLatency, subLatency, mulLatency, divLatency, loadLatency, storeLatency, pc,
							false);
//System.out.println("issue"+issued);
				if (issued) {
					pc++;
					// System.out.println("here3");
				}
				clock++;
				System.out.println("Registers content:");
				for (int i = 0; i < registerFile.length; i++) {
					System.out.println("F" + i + " : " + registerFile[i]);
				}
				System.out.println();
				System.out.println("Instruction Memory content: ");
				for (int i = 0; i < instructionMemory.length; i++) {
					if (instructionMemory[i] != null) {
						System.out.println(i + " : " + instructionMemory[i]);
					}
				}
				System.out.println();
//				System.out.println("Data Memory content: ");
//				for (int i = 0; i < dataMemory.length; i++) {
//					if (dataMemory[i] != 0) {
//						System.out.println(i + " : " + dataMemory[i]);
//					}
//				}
				System.out.println("Store Buffer:");
				for (int i = 0; i < SB.length; i++) { // this equals to the row in our matrix.
					for (int j = 0; j < SB[i].length; j++) { // this equals to the column in each row.
						System.out.print(SB[i][j] + " ");
					}
					System.out.println(); // change line on console as row comes to end in the matrix.
				}
				System.out.println("Load Buffer:");
				for (int i = 0; i < LB.length; i++) { // this equals to the row in our matrix.
					for (int j = 0; j < LB[i].length; j++) { // this equals to the column in each row.
						System.out.print(LB[i][j] + " ");
					}
					System.out.println(); // change line on console as row comes to end in the matrix.
				}
				System.out.println("MUL RS:");
				for (int i = 0; i < MulRS.length; i++) { // this equals to the row in our matrix.
					for (int j = 0; j < MulRS[i].length; j++) { // this equals to the column in each row.
						System.out.print(MulRS[i][j] + " ");
					}
					System.out.println(); // change line on console as row comes to end in the matrix.
				}
				System.out.println("Add RS:");
				for (int i = 0; i < AddRS.length; i++) { // this equals to the row in our matrix.
					for (int j = 0; j < AddRS[i].length; j++) { // this equals to the column in each row.
						System.out.print(AddRS[i][j] + " ");
					}
					System.out.println(); // change line on console as row comes to end in the matrix.
				}
				System.out.println("ExecutionTables: ");
				for (int i = 0; i < ExecutionTable.length; i++) { // this equals to the row in our matrix.
					for (int j = 0; j < ExecutionTable[i].length; j++) { // this equals to the column in each row.
						if(i==0)
						System.out.print(ExecutionTable[i][j] + "      ");
						else
							System.out.print(ExecutionTable[i][j] + "             ");
					} 
					System.out.println(); // change line on console as row comes to end in the matrix.
				}
				int c = 0;
				for (int i = 0; i < ExecutionTable.length; i++) {
					if (ExecutionTable[i][8] == null) {
						c++;
					}
				}
				if (c == 0) {
					finished = true;
				}
				flagA1 = false;
				flagA2 = false;
				flagA3 = false;
				flagM1 = false;
				flagM2 = false;
				flagS1 = false;
				flagS2 = false;
				flagS3 = false;
				
				System.out.println(
						"----------------------------------------------------------------------------------------------------------------------------------------");
			}
		}
// ExecutionTable[0][0]="Instruction";
// ExecutionTable[0][1]="Destination";
// ExecutionTable[0][2]="Source1";
// ExecutionTable[0][3]="Source2";
// ExecutionTable[0][4]="Station";
// ExecutionTable[0][5]="Issue";
// ExecutionTable[0][6]="ExecutionStart"; 
// ExecutionTable[0][7]="ExecutionEnd"; 
// ExecutionTable[0][8]="Write Result"; 
//ISSUE
// while(true) {
// System.out.println("Clock Cycle: "+ clock);
// System.out.println("PC: "+pc);
// if(!executequeue.isEmpty()) {
// byte o=(byte) executequeue.remove();
// int reg1 =(int) executequeue.remove();
// int reg2=(int) executequeue.remove();
// byte op1=(byte) executequeue.remove();
// byte op2=(byte) executequeue.remove();
// byte im=(byte) executequeue.remove();
// short p=(short)executequeue.remove();
// 
// execute(o,reg1,reg2,op1,op2,im,p);
// 
// }
// else if(flag==true && branch==false) {
// System.out.println("The program is done!");
// break;
// }
// if(!decodequeue.isEmpty()) {
// short instr=(short)decodequeue.remove();
// short p=(short)decodequeue.remove();
// decode(instr,p);
// }
// if(instructionMemory[pc]!=0) {
// if(branch==true) {
//    fetch(oldp);
//    flag=false;
// }
// else
// fetch(pc);
// System.out.println("PC updated: "+pc);
// }
// else {
// flag=true;
// }
// if(branch==true) {
// branch=false;
// if(!executequeue.isEmpty()) {
// executequeue.remove();
// executequeue.remove();
// executequeue.remove();
// executequeue.remove();
// executequeue.remove();
// executequeue.remove();
// executequeue.remove();
// }
// if(!decodequeue.isEmpty()) {
// decodequeue.remove();
// decodequeue.remove();
// }
// }
// clock++;
// System.out.println("-------------------------------------------------------------");
// }
// 

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Tomasulo t = new Tomasulo();
		System.out.println("Please enter the latency of ADD.D instructions");
		Scanner al = new Scanner(System.in);
		int addLatency = (int) al.nextInt();
		System.out.println("Please enter the latency of SUB.D instructions");
		Scanner sl = new Scanner(System.in);
		int subLatency = sl.nextInt();
		System.out.println("Please enter the latency of MUL.D instructions");
		Scanner ml = new Scanner(System.in);
		int mulLatency = ml.nextInt();
		System.out.println("Please enter the latency of DIV.D instructions");
		Scanner dl = new Scanner(System.in);
		int divLatency = dl.nextInt();
		System.out.println("Please enter the latency of L.D instructions");
		Scanner ll = new Scanner(System.in);
		int loadLatency = ll.nextInt();
		System.out.println("Please enter the latency of S.D instructions");
		Scanner stl = new Scanner(System.in);
		int storeLatency = stl.nextInt();
		for (int i = 0; i < registerFile.length; i++) {
			registerFile[i] = i + "";
		}
		for (int i = 0; i < dataMemory.length; i++) {
			dataMemory[i] = i;
		}
//pass latencies
		readfile(addLatency, subLatency, mulLatency, divLatency, loadLatency, storeLatency);
//		System.out.println("Registers content:");
//		for (int i = 0; i < registerFile.length; i++) {
//			System.out.println("F" + i + " : " + registerFile[i]);
//		}
//		System.out.println();
//		System.out.println("Instruction Memory content: ");
//		for (int i = 0; i < instructionMemory.length; i++) {
//			if (instructionMemory[i] != null) {
//				System.out.println(i + " : " + instructionMemory[i]);
//			}
//		}
//		System.out.println();
//		System.out.println("Data Memory content: ");
//		for (int i = 0; i < dataMemory.length; i++) {
//			if (dataMemory[i] != 0) {
//				System.out.println(i + " : " + dataMemory[i]);
//			}
//		}
//		System.out.println("Store Buffer:");
//		for (int i = 0; i < SB.length; i++) { // this equals to the row in our matrix.
//			for (int j = 0; j < SB[i].length; j++) { // this equals to the column in each row.
//				System.out.print(SB[i][j] + " ");
//			}
//			System.out.println(); // change line on console as row comes to end in the matrix.
//		}
//		System.out.println("Load Buffer:");
//		for (int i = 0; i < LB.length; i++) { // this equals to the row in our matrix.
//			for (int j = 0; j < LB[i].length; j++) { // this equals to the column in each row.
//				System.out.print(LB[i][j] + " ");
//			}
//			System.out.println(); // change line on console as row comes to end in the matrix.
//		}
//		System.out.println("MUL RS:");
//		for (int i = 0; i < MulRS.length; i++) { // this equals to the row in our matrix.
//			for (int j = 0; j < MulRS[i].length; j++) { // this equals to the column in each row.
//				System.out.print(MulRS[i][j] + " ");
//			}
//			System.out.println(); // change line on console as row comes to end in the matrix.
//		}
//		System.out.println("Add RS:");
//		for (int i = 0; i < AddRS.length; i++) { // this equals to the row in our matrix.
//			for (int j = 0; j < AddRS[i].length; j++) { // this equals to the column in each row.
//				System.out.print(AddRS[i][j] + " ");
//			}
//			System.out.println(); // change line on console as row comes to end in the matrix.
//		}
//		System.out.println("ExecutionTables: ");
//		for (int i = 0; i < ExecutionTable.length; i++) { // this equals to the row in our matrix.
//			for (int j = 0; j < ExecutionTable[i].length; j++) { // this equals to the column in each row.
//				System.out.print(ExecutionTable[i][j] + " ");
//			}
//			System.out.println(); // change line on console as row comes to end in the matrix.
//		}
	}

}
