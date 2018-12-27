package io.mosip.registration.builder;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
/**
 * Generic Builder Pattern
 * 
 * @author M1045980
 *
 * @param <T>
 */
public class Builder<T> {
    private T instance;
    private boolean ifCond = true; // default
    /**
     * Constructor Initialization
     * 
     * @param clazz
     */
    public Builder(Class<T> clazz){
       try {
           instance = clazz.newInstance();
       } catch (InstantiationException | IllegalAccessException e) {
           e.printStackTrace();
       } 
    }
    
    /**
     * Set the parameter with desired value
     * 
     * @param setter
     * @return
     */
    public Builder<T> with(Consumer<T> setter){
       if(ifCond)
           setter.accept(instance);
       return this;
    }
    
    /**
     * Get the initialized instance
     * 
     * @return
     */
    public T get(){
       return instance;
    }
    
    /**
     * Build the instance
     * 
     * @param clazz
     * @return
     */
    public static <T> Builder<T> build(Class<T> clazz) {
       return new Builder<>(clazz);
    }
    
    /**
     * Build Based on condition
     * 
     * @param condition
     * @return
     */
    public Builder<T> If(BooleanSupplier condition){
       this.ifCond = condition.getAsBoolean();
       return this;
    }
    
    /**
     * Condition end to the Build
     * 
     * @return
     */
    public Builder<T> endIf(){
        this.ifCond = true;
        return this;
    }
 }
