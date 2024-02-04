package saltsheep.keylib.key;

import com.google.common.collect.Maps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Date;
import java.util.Map;

@EventBusSubscriber
public class PlayerKeyHandler {

	private static Map<String,Map<Integer,State>> keyState = Maps.newHashMap();
	
	private static void checkMap(String player) {
		if(!keyState.containsKey(player))
			keyState.put(player, Maps.newHashMap());
	}
	
	private static void checkKey(String player,int key) {
		if(!keyState.get(player).containsKey(key))
			keyState.get(player).put(key, new State());
	}
	
	public static void reset(String player) {
		keyState.remove(player);
	}
	
	public static void resetKey(String player,int key) {
		checkMap(player);
		keyState.get(player).put(key, new State());
	}

	/**
	 * @Param time history result,don't touch it,you can just input 0.
	 * @return isFirstDown
	 */
	public static boolean keyDown(String player,int key,long time) {
	    checkMap(player);
	    checkKey(player,key);
		State state = keyState.get(player).get(key);
		time = System.currentTimeMillis();
		boolean isFirstDown = false;
		if(state.downTime<=state.upTime) {
			state.firstDownTime=time;
			isFirstDown = true;
			MinecraftForge.EVENT_BUS.post(new PlayerKeyDownEvent(player,key,time));
		}
		state.downTime=time;
		return isFirstDown;
	}
	
	public static void keyUp(String player,int key,long time) {
		checkMap(player);
		checkKey(player,key);
		time = System.currentTimeMillis();
		keyState.get(player).get(key).upTime=time;
		MinecraftForge.EVENT_BUS.post(new PlayerKeyUpEvent(player,key,time));
	}
	
	public static boolean isKeyDown(String player,int key) {
		if(!keyState.containsKey(player)) {
			keyState.put(player, Maps.newHashMap());
			return false;
		}
		Map<Integer,State> stateIn = keyState.get(player);
		if(!stateIn.containsKey(key))
			return false;
		else {
			State state = stateIn.get(key);
			return state.downTime>state.upTime;
		}
	}
	
	public static boolean isKeyClick(String player,int key) {
		if(!keyState.containsKey(player))
			return false;
		Map<Integer,State> stateIn = keyState.get(player);
		if(!stateIn.containsKey(key))
			return false;
		State state = stateIn.get(key);
		return state.downTime<state.upTime&&state.upTime-state.firstDownTime<500&&new Date().getTime()-state.upTime<100;
	}
	
	public static boolean isKeyLongPressed(String player,int key) {
		if(!keyState.containsKey(player)) {
			keyState.put(player, Maps.newHashMap());
			return false;
		}
		Map<Integer,State> stateIn = keyState.get(player);
		if(!stateIn.containsKey(key))
			return false;
		else {
			State state = stateIn.get(key);
			return state.downTime>state.upTime&&state.downTime-state.firstDownTime>=500;
		}
	}
	
	public static State getState(String player,int key) {
		checkMap(player);
		checkKey(player,key);
		return keyState.get(player).get(key);
	}

	public static class State{
		public long firstDownTime=0;
		public long downTime=0;
		public long upTime=0;
	}
	
}
