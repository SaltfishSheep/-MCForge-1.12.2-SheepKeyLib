package saltsheep.keylib.key;

import java.util.Date;
import java.util.Map;

import saltsheep.keylib.key.PlayerKeyHandler.State;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import saltsheep.keylib.SheepKeyLibConfig;
import saltsheep.keylib.key.network.DataKeyState;
import saltsheep.keylib.key.network.DataResetKey;

@EventBusSubscriber
public class PlayerKeyClientHandler {

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
		}
		state.downTime=time;
		return isFirstDown;
	}
	
	public static void keyUp(String player,int key,long time) {
		checkMap(player);
		checkKey(player,key);
		time = System.currentTimeMillis();
		keyState.get(player).get(key).upTime=time;
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
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public static void onKeyInput(KeyInputEvent event) {
		if(Minecraft.getMinecraft().player!=null) {
			int key = Keyboard.getEventKey();
			boolean isDown = Keyboard.getEventKeyState();
			if(isDown) {
				if (!PlayerKeyClientHandler.keyDown(Minecraft.getMinecraft().player.getName(), key, System.currentTimeMillis()))
					return;//*如果是按下按键，并且不是第一次按下，就不再发送给服务端，节省网络资源
			}else {
				PlayerKeyClientHandler.keyUp(Minecraft.getMinecraft().player.getName(), key, System.currentTimeMillis());
			}
			DataKeyState.inputByClient(key, isDown);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public static void onMouseInput(MouseInputEvent event) {
		if(SheepKeyLibConfig.recordMouse==false)
			return;
		if(Minecraft.getMinecraft().player!=null)
			DataKeyState.inputByClient(Mouse.getEventButton()-100, Mouse.getEventButtonState());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public static void onGuiOpen(GuiOpenEvent event) {
		if(Minecraft.getMinecraft().player!=null)
			DataResetKey.resetInputByClient();
	}
	
}
