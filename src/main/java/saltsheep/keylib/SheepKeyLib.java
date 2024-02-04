package saltsheep.keylib;

import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import saltsheep.keylib.key.network.NetworkHandler;

@Mod(modid = SheepKeyLib.MODID, name = SheepKeyLib.NAME, version = SheepKeyLib.VERSION, useMetadata = true , acceptableRemoteVersions = "*")
public class SheepKeyLib
{
    public static final String MODID = "sheepkeylib";
    public static final String NAME = "SheepKeyLib";
    public static final String VERSION = "1.3";
    public static SheepKeyLib instance;

    private static Logger logger;

    public SheepKeyLib() {
    	instance = this;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        NetworkHandler.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
    }
    
    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event){
	}
    
    public static Logger getLogger() {
    	return logger;
    }
    
    public static MinecraftServer getMCServer() {
    	return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
    
    public static void printError(Throwable error) {
        StringBuilder messages = new StringBuilder();
        for(StackTraceElement stackTrace : error.getStackTrace()) {
            messages.append("\n").append(stackTrace.toString());
        }
        logger.error("Warning!The mod of me(Saltfish_Sheep) meet an error:\n"+"Error Type:"+error.getClass()+"-"+error.getMessage()+"\n"+messages);
    }
    
    public static void info(String str) {
    	logger.info(str);
    }
    
    public static void info(Object obj) {
    	if(obj == null)
    		logger.info("null has such obj.");
    	else
    		logger.info(obj.toString());
    }
    
    
}
