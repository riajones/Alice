public class AI_Main{
	static Alice alice;
	public static void main(String[] args){
		System.out.println("\n\n\n");
		if(args.length > 0){
			int graphics_mode = (int)(args[0].charAt(0)) - 48;
			if(graphics_mode == 0) alice = new Alice(false);
			else if(graphics_mode == 1) alice = new Alice(true);
			else alice = new Alice(false);
		} else alice = new Alice(false);
	}

}