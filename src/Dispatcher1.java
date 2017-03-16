import java.util.ArrayList;
public class Dispatcher1 extends Dispatcher{
	//inherit PrintSequence but not the others
	private double[][] finSequenceA = super.getFinSequence();// time outcome
	public String[] ALS_Strings = new String[101];//ALS outcome
	public int num3 = 0;
	
	public int num1 = 0;//the numbers of main requests.
	public Dispatcher1(){
		int i;
		int j;
		for (i = 0; i < 101; i++){
			for( j = 0; j < 3; j++){
				this.finSequenceA[i][j] = 0;
			}
		}
	}//construction
	public String toString(ArrayList<Request> lines){
		String str = "";
		int length = lines.size();
		
		if (lines.get(0).getCategory().equals("FR")){
			str += ("(" + "FR," + Integer.toString(lines.get(0).getFloor().getCurFloor()) + "," + lines.get(0).getFloor().getUpOrDown() + "," +
					Integer.toString(lines.get(0).getFloor().getReqTime()) + ")");
		}
		else if(lines.get(0).getCategory().equals("ER")){
			str += ("(" + "ER," + Integer.toString(lines.get(0).getElevator().getTarFloor()) + ","  +
					Integer.toString(lines.get(0).getElevator().getReqTime()) + ")");
		}
		str += "(";
		for(int i = 1; i < length; i++){
			Request temp = new Request();
			if (lines.get(i) instanceof Request){
				temp = (Request) lines.get(i);
				if (temp.getCategory().equals("FR")){
					str += ("(" + "FR," + Integer.toString(temp.getFloor().getCurFloor()) + "," + temp.getFloor().getUpOrDown() + "," +
							Integer.toString(temp.getFloor().getReqTime()) + ")");
				}
				else if(temp.getCategory().equals("ER")){
					str += ("(" + "ER," + Integer.toString(temp.getElevator().getTarFloor()) + ","  +
							Integer.toString(temp.getElevator().getReqTime()) + ")");
				}
			}
		}
		str += ")";
		if (str != "" && length == 1) return str.substring(0, str.length() - 2);
		return str;
	}// make a toStringLines function
	public boolean allUsed(Request allRequest[], int num1){
		int i = 0;
		for (i = 0; i < num1; i++){
			if (allRequest[i].used == false)
				return false;
		}
		return true;
	}//
	public void PrintSequence(int n, double [][]finSequence){
		int i = 0;
		for (i = 0; i < n; i++){
			System.out.print("(" + (int)finSequence[i][0] + ",");//
			if (finSequence[i][1] == 1) System.out.print("UP" + ",");
			else if (finSequence[i][1] == -1) System.out.print("DOWN" + ",");
			else if (finSequence[i][1] == 0) System.out.print("STAY,");
			System.out.println(finSequence[i][2] + ")");
		}
	}//
	public void ALS_Schedule(String string){
		RequestQueue finalQueue = new RequestQueue(string);//the queue
		
		Request allRequest[] = finalQueue.getAllRequest();//all the request
		int num1 = finalQueue.getNum();
		
		/*Request reqList[] = new Request[200]; 
		int head = 0, rear = 0;*/
		
		ArrayList<Request> ALS_Lines = new ArrayList<Request>();//the temporary storage array of the Requests
		
		CurrentStatus curStatus = new CurrentStatus();// status register
		int i = 0, j = 0;
		
		Harmony harmonyi = null, harmonyj = null;
		
		double [][]StopSequence = new double[101][3];
		int num2 = 0;
		
		int sameFloorFlag = 0;
		
		while (true){
			if(this.allUsed(allRequest, num1))
				break;//
			for(i = 0; i < num1; i++){
				if(allRequest[i].used == false){
					harmonyi = new Harmony(allRequest[i]);
					if (i == 0){
						curStatus.curFloor = 0;//1
						curStatus.dirStatus = "UP";
						curStatus.tarFloor = harmonyi.floor;
						curStatus.time = -0.5;//0
					}
					else{
						curStatus.curFloor = curStatus.curFloor;
						curStatus.dirStatus = (harmonyi.floor < curStatus.curFloor) ? "DOWN" : 
							(harmonyi.floor > curStatus.curFloor) ? "UP" : "STAY"/*curStatus.dirStatus*/;
						curStatus.tarFloor = harmonyi.floor;
						curStatus.time = (harmonyi.time <= curStatus.time) ? curStatus.time ://remain to be seen
							(harmonyi.time > curStatus.time) ? harmonyi.time : 0 ;
					}
					ALS_Lines.add(allRequest[i]);// main request has been added in.
					break;
				}
			}//
			while(true){
				if (curStatus.dirStatus.equals("UP")) curStatus.goUp(1);
				else if (curStatus.dirStatus.equals("DOWN")) curStatus.goDown(1);
				sameFloorFlag = 0;
				//
				if(curStatus.curFloor == curStatus.tarFloor){
					StopSequence[num2][0] = curStatus.curFloor;
					if (curStatus.dirStatus.equals("UP"))
						StopSequence[num2][1] = 1;
					else if (curStatus.dirStatus.equals("DOWN"))
						StopSequence[num2][1] = -1;
					else if (curStatus.dirStatus.equals("STAY"))
						StopSequence[num2][1] = 0;
					StopSequence[num2][2] = curStatus.time;
					num2++;
					
					allRequest[i].used = true;
					curStatus.time += 1;
					break;
				}
				if (i == num1 - 1){
					harmonyj = new Harmony(allRequest[i]);
					if (harmonyj.floor == curStatus.curFloor){
						if ((harmonyj.type.equals("FR") && harmonyj.direction.equals(curStatus.dirStatus)) || (harmonyj.type.equals("ER"))) {
							if (harmonyj.time < curStatus.time){
								ALS_Lines.add(allRequest[j]);//
								StopSequence[num2][0] = curStatus.curFloor;
								if (curStatus.dirStatus.equals("UP"))
									StopSequence[num2][1] = 1;
								else if (curStatus.dirStatus.equals("DOWN"))
									StopSequence[num2][1] = -1;
								else if (curStatus.dirStatus.equals("STAY"))
									StopSequence[num2][1] = 0;
								StopSequence[num2][2] = curStatus.time;
								num2++;
								
								allRequest[j].used = true;
								curStatus.time += 1;
								break;
							}
						}
					}
					continue;
				}// the last request
				else{
					for (j = i + 1; j < num1; j++){
						harmonyj = new Harmony(allRequest[j]);
						if (harmonyj.floor == curStatus.curFloor){
							if ((harmonyj.type.equals("FR") && harmonyj.direction.equals(curStatus.dirStatus)) || (harmonyj.type.equals("ER"))) {
								if (harmonyj.time < curStatus.time){
									ALS_Lines.add(allRequest[j]);//
									allRequest[j].used = true;
									if(sameFloorFlag == 0){
										;
										sameFloorFlag++;
									}
									else {
										continue;
									}
									StopSequence[num2][0] = curStatus.curFloor;
									if (curStatus.dirStatus.equals("UP"))
										StopSequence[num2][1] = 1;
									else if (curStatus.dirStatus.equals("DOWN"))
										StopSequence[num2][1] = -1;
									else if (curStatus.dirStatus.equals("STAY"))
										StopSequence[num2][1] = 0;
									StopSequence[num2][2] = curStatus.time;
									num2++;
									curStatus.time += 1;
								}
							}
						}
					}
				}
			}// counting the floors.	
			this.ALS_Strings[this.num3] = toString(ALS_Lines);
			this.num3++;
			ALS_Lines.clear();
		}//main request has accomplished. so store it;
		System.out.println();
		for(i = 0; i < this.num3; i++){
			System.out.println(this.ALS_Strings[i]);
		}
		System.out.println();
		this.PrintSequence(num2, StopSequence);
	}//generate the ALS_Strings, and every elements in this array should be FoolScheduled
	
	
}
/*
		for (i = 0; i < finalQueue.getNum(); i++){
			if (finalQueue.getRequest(i).used) continue;
			tempRequesti = finalQueue.getRequest(i);
			if (tempRequesti.getCategory() == "ER"){
				tempFloori = tempRequesti.getElevator().getTarFloor();
				tempReqTimei = tempRequesti.getElevator().getReqTime();
			}
			else if (tempRequesti.getCategory() == "FR"){
				tempFloori = tempRequesti.getFloor().getCurFloor();
				tempReqTimei = tempRequesti.getFloor().getReqTime();
			}// get temporary data of the i 
			if (curStatus.curFloor == 0 && curStatus.tarFloor == 0){
				curStatus.curFloor = 1;
				curStatus.dirStatus = "UP";
				curStatus.tarFloor = finalQueue.getRequest(i).getFloor().getCurFloor();
				ALS_Lines.add(tempRequestj);
			}//the beginning of the ArrayList
			for( j = i + 1; j < finalQueue.getNum(); j++){
				tempRequestj = finalQueue.getRequest(j);
				if (tempRequestj.getCategory().equals("ER")){
					tempFloorj = tempRequestj.getElevator().getTarFloor();
					tempReqTimej = tempRequestj.getElevator().getReqTime();
				}
				else if (tempRequestj.getCategory().equals("FR")){
					tempFloorj = tempRequestj.getFloor().getCurFloor();
					tempReqTimej = tempRequestj.getFloor().getReqTime();
					tempDirectionj = tempRequestj.getFloor().getUpOrDown();
				}// get temporary data of j
				double timeLimit = Math.abs(curStatus.tarFloor - curStatus.curFloor) * 0.5;
				if (tempRequestj.getCategory().equals("FR") && tempDirectionj.equals(curStatus))
					continue;//FR请求,但方向不同，不捎带.
				else if (tempFloorj <= curStatus.curFloor)
					continue;//所有请求，方向不同，不捎带.
				else if ((tempReqTimei + timeLimit) < tempReqTimej)
					continue;//时间太晚，过去了.不捎带
				else {
					if(tempFloorj >= curStatus.curFloor && tempFloorj <= curStatus.tarFloor){
						ALS_Lines.add(tempRequestj);
						finalQueue.getRequest(j).used = true;
						curStatus.curFloor = tempFloorj;
					}
					else 
						continue;
				}//能被捎带的	
			}// traversal of the latter ones finding the requests that can be added.
			this.ALS_Strings[this.num1] = toStringLines(ALS_Lines);
			this.num1++;
			
			for (l = 0; i < ALS_Lines.size(); i++){
				;
			}
			
			//make it into the string and add it to ALS_Lines[];
		}*/
/*
for(i = 0; i < finalQueue.getNum(); i++){
	if (finalQueue.getRequest(i).used) continue;
	tempRequesti = finalQueue.getRequest(i);
	if (tempRequesti.getCategory() == "ER"){
		tempFloori = tempRequesti.getElevator().getTarFloor();
		tempReqTimei = tempRequesti.getElevator().getReqTime();
		tempTypei = "ER";
		tempDirectioni = "-";
	}
	else if (tempRequesti.getCategory() == "FR"){
		tempTypei = "FR";
		tempFloori = tempRequesti.getFloor().getCurFloor();
		tempReqTimei = tempRequesti.getFloor().getReqTime();
		tempDirectioni = tempRequesti.getFloor().getUpOrDown();
	}// get temporary data of the i 
	if (curStatus.curFloor == 0 && curStatus.tarFloor == 0){
		curStatus.curFloor = 1;
		curStatus.dirStatus = "UP";
		curStatus.tarFloor = tempFloori;//finalQueue.getRequest(i).getFloor().getCurFloor()
		ALS_Lines.add(tempRequesti);
	}//the beginning of the ArrayList
	
	for( j = i + 1; j < finalQueue.getNum(); j++){
		
		tempRequestj = finalQueue.getRequest(j);
		if (tempRequestj.getCategory().equals("ER")){
			tempTypej = "ER";
			tempFloorj = tempRequestj.getElevator().getTarFloor();
			tempReqTimej = tempRequestj.getElevator().getReqTime();
			tempDirectionj = "-";
		}
		else if (tempRequestj.getCategory().equals("FR")){
			tempTypej = "FR";
			tempFloorj = tempRequestj.getFloor().getCurFloor();
			tempReqTimej = tempRequestj.getFloor().getReqTime();
			tempDirectionj = tempRequestj.getFloor().getUpOrDown();
		}// get temporary data of j
		
	}// traversal of the latter ones finding the requests that can be added.
}*/


/*Request tempRequesti = new Request();
Request tempRequestj = new Request();
Request tempRequestl = new Request();
int tempFloori = 0, tempReqTimei = 0;
int tempFloorj = 0, tempReqTimej = 0;
String tempDirectioni = "";
String tempDirectionj = "";
String tempTypei = "";
String tempTypej = "";
double timeLimit = 0;*/