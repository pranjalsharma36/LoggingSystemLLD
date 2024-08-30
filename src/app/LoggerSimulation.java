package app;


import java.io.*;
import java.util.*;
import java.lang.*;

public class LoggerSimulation {
	public static void main (String[] args) {
		System.out.println("GfG!");
		
		Logger logger = Logger.getInstance();
		
		logger.info("Hi, this is pranjal");
		
		logger.debug("Hi Pranjal");
	}
}

enum LoggerLevel{
    
    INFO,
    DEBUG,
    ERROR
}
class Logger{
    // singleton class
    
    private static Logger logger;
    private static AbstractLogger chainLogger;
    private static LoggerTarget loggerTarget;
    
    public static Logger getInstance(){
        
        synchronized(Logger.class){
            if(logger == null){
                logger = new Logger();
                chainLogger = ChainManager.createChainLogger();
                loggerTarget = new LoggerTarget();
            }
        }
        
        return logger;
    }
    
    // log functions
    public void info(String message){
        chainLogger.process(LoggerLevel.INFO, message, loggerTarget);
    }
    
    public void debug(String message){
        chainLogger.process(LoggerLevel.DEBUG, message, loggerTarget);
    }
    
    public void error(String message){
        chainLogger.process(LoggerLevel.ERROR, message, loggerTarget);
    }
}

// we will create a separate chain manager which will be responsible for creating an instance of chain

abstract class AbstractLogger{
    
    private AbstractLogger nextLogger;
    private LoggerLevel level;
    
    // constructor
    AbstractLogger(LoggerLevel level){
        this.level = level;
        
    }
    
    public void setNextLogger(AbstractLogger nextLogger){
        this.nextLogger = nextLogger;
    }
    
    public void process(LoggerLevel level, String message, LoggerTarget loggerTarget){
        
        if(this.level == level){
            display(message, loggerTarget);
        }
        
        else if(nextLogger != null){
            nextLogger.process(level, message, loggerTarget);
        }
    }
    
    public abstract void display(String message, LoggerTarget loggerTarget);
}

class ChainManager{
    
    public static AbstractLogger createChainLogger(){
        
        AbstractLogger infoLogger = new InfoLogger();
        AbstractLogger debugLogger = new DebugLogger();
        
        infoLogger.setNextLogger(debugLogger);
        return infoLogger;
    }
}



class InfoLogger extends AbstractLogger{
    
    public InfoLogger(){
        super(LoggerLevel.INFO);
    }
    
    public void display(String message, LoggerTarget target){
        // System.out.println("INFO : " + message);
        
        target.notify(LoggerLevel.INFO, "INFO : " + message);
    }
}

class DebugLogger extends AbstractLogger{
    
    public DebugLogger(){
        super(LoggerLevel.DEBUG);
    }
    
    public void display(String message, LoggerTarget target){
        target.notify(LoggerLevel.DEBUG, "DEBUG : " + message);
    }
}


// observer Design Pattern


interface IObserver{
    public void log(String message);
}

class FileLogger implements IObserver{
    
    public void log(String message){
        System.out.println(" Writing to file : " + message);
    }
}

class DatabaseLogger implements IObserver{
    
    public void log(String message){
        System.out.println(" Writing to database : " + message);
    }
}

class ConsoleLogger implements IObserver{
    
    public void log(String message){
        System.out.println(" Writing to console : " + message);
    }
}

interface IObservee{
    public void register(LoggerLevel level, IObserver target);
    
    public void deregister(LoggerLevel level, IObserver target);
    
    public void notify(LoggerLevel level, String message);
}

class LoggerTarget implements IObservee{
    
    private HashMap<LoggerLevel, List<IObserver>> logTargetCatalog;
    
    // constructor
    LoggerTarget(){
        
        // catalog creation and initialization
        
        logTargetCatalog = new HashMap<>();
        List<IObserver> infoTargets = new ArrayList<>();
        List<IObserver> debugTargets = new ArrayList<>();
        
        IObserver fileLogger = new  FileLogger();
        IObserver databaseLogger = new DatabaseLogger();
        IObserver consoleLogger = new ConsoleLogger();
        
        infoTargets.add(fileLogger);
        infoTargets.add(consoleLogger);
        
        debugTargets.add(databaseLogger);
        debugTargets.add(consoleLogger);
        
        logTargetCatalog.put(LoggerLevel.INFO, infoTargets);
        logTargetCatalog.put(LoggerLevel.DEBUG, debugTargets);
    
        
    }
    
    public void register(LoggerLevel level, IObserver target){
        if(!logTargetCatalog.containsKey(level));
            logTargetCatalog.put(level, new ArrayList<>());
        
        logTargetCatalog.get(level).add(target);
    }
    
    public void deregister(LoggerLevel level, IObserver target){
        
        
        logTargetCatalog.get(level).remove(target);
    }
    
    public void notify(LoggerLevel level, String message){
        
        List<IObserver> targets = logTargetCatalog.get(level);
        
        for(IObserver target : targets){
            target.log(message);
        }
    }
    
}

/*
object identification :

Logger :- singleton obj

chaimOfLogger => respobsible for handling the logger request

 loggerTarget => targetCatalogManager => HashMap<LogLevel, List<Target>> register, deregister, notify(loggerLevel, message)

Target = > overserve => doSomething()




*/