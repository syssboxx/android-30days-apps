/*
 * Copyright (c) 2002 Scott Clee (Scott_Clee@uk.ibm.com), Software Engineer, IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package tetrisbean;


/**
 * The GameEvent class.
 *
 * @author Scott Clee
 */
public class GameEvent extends Event
{
    public static final int START = 0;
    public static final int END   = 1;

    private final int fType;

    /**
     * Create a GameEvent.
     *
     * @param type   The type of event. Either START or END.
     * @param source The source of the Event.
     */
    public GameEvent(int type)
    {
		super(null);
        fType   = type;
    }

    /**
     * Returns the type of the event.
     *
     * @return The type of the event.
     */
    public int getType()
    {
        return fType;
    }
}
