package stickman.figure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import stickman.render.Utils;

public class Stances {

	private HashMap<String, float[]> stickAnims;
	private HashMap<String, float[]> animsLengths;
	
	private HashMap<String, float[]> enemyAnims;
	private HashMap<String, float[]> enemyLengths;

	public Stances() throws Exception {
		stickAnims = new HashMap<>();
		animsLengths = new HashMap<>();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream("/stances/stickman")));

		String line;
		String id = "";
		while ((line = reader.readLine()) != null) {
			line = line.replace(" ", "");
			if(line.startsWith("part")) {
				id = line.substring(4,6);
			}else if(line.startsWith("move")) {
				id += line.substring(4,6);
			}else if(line.startsWith("rot")) {
				stickAnims.put(id, strArrToFloArr(line.substring(3).split(",")));
			}else if(line.startsWith("length")){
				animsLengths.put(id, strArrToFloArr(line.substring(6).split(",")));	
				id = id.substring(0, 2);
			}			
		}
		reader.close();
		
		
		enemyAnims = new HashMap<>();
		enemyLengths = new HashMap<>();

		reader = new BufferedReader(
				new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream("/stances/enemy")));

		id = "";
		while ((line = reader.readLine()) != null) {
			line = line.replace(" ", "");
			if(line.startsWith("part")) {
				id = line.substring(4,6);
			}else if(line.startsWith("move")) {
				id += line.substring(4,6);
			}else if(line.startsWith("rot")) {
				enemyAnims.put(id, strArrToFloArr(line.substring(3).split(",")));
			}else if(line.startsWith("length")){
				enemyLengths.put(id, strArrToFloArr(line.substring(6).split(",")));	
				id = id.substring(0, 2);
			}			
		}
		
		reader.close();
	}
	
	private float[] strArrToFloArr(String[] split) {
		float[] floArr = new float[split.length];
		for (int i = 0; i < split.length; i++) {
			floArr[i] = Float.parseFloat(split[i]);
		}
		return floArr;
	}

	public float[] getStickAnims(String part, String move, boolean player) {
		return player ? stickAnims.get(part + move) : enemyAnims.get(part + move);
	}

	public float[] getAnimLength(String part, String move, boolean player) {
		return player ? animsLengths.get(part + move) : enemyLengths.get(part + move);
	}
}
