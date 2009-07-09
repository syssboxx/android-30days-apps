/*
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://www.bakhtiyor.com/)
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
 * The BoardEvent class.
 *
 * @author Scott Clee
 */
public class BoardEvent extends Event
{
    private final Object fSource;

    /**
     * Create a BoardEvent.
     *
     * @param source The source of the event.
     */
    public BoardEvent(Object source)
    {
		super(source);
	    fSource = source;
    }

    /**
     * Returns the source of the event.
     *
     * @return The source of the event.
     */
    public Object getSource()
    {
	    return fSource;
    }
}
