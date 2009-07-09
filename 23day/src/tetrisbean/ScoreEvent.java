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
 * The ScoreEvent class.
 *
 * @author Scott Clee
 */
public class ScoreEvent extends Event
{
    private final int fScore;

    /**
     * Create a ScoreEvent.
     *
     * @param score  The new score.
     */
    public ScoreEvent(int score)
    {
		super(null);
	    fScore = score;
    }

    /**
     * Returns the new score.
     *
     * @return The new score.
     */
    public int getScore()
    {
	    return fScore;
    }
}
