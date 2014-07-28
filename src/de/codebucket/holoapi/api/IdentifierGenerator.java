package de.codebucket.holoapi.api;

public class IdentifierGenerator 
{
	public IdentifierGenerator() {}
	
	//TAG ID GENERATOR
	private volatile static int SHARED_ID = Short.MAX_VALUE;
	private volatile static int SHARED_SIMPLE_ID = Short.MIN_VALUE;

	public static int nextId(int counter) 
	{
	    return nextId(counter, false);
	}

	public static int nextSimpleId(int counter)
	{
	    return nextId(counter, true);
	}

	private static int nextId(int counter, boolean simple) 
    {
        int firstId = simple ? ++SHARED_SIMPLE_ID : ++SHARED_ID;
        if (simple)
        {
            for (int i = 0; i <= (counter * Hologram.TAG_ENTITY_MULTIPLIER); i++) 
            {
                if ((firstId + i) > 0) 
                {
                    SHARED_SIMPLE_ID = Short.MIN_VALUE;
                    return nextId(counter, true);
                }
            }
            SHARED_SIMPLE_ID += counter * Hologram.TAG_ENTITY_MULTIPLIER;
        } 
        else 
        {
            SHARED_ID += counter * Hologram.TAG_ENTITY_MULTIPLIER;
        }
        return firstId;
    }
}
