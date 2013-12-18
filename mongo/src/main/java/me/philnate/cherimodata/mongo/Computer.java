/**
 *    Copyright [cherimojava (http://github.com/philnate/cherimojava.git)]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package me.philnate.cherimodata.mongo;

import me.philnate.cherimodata.mongo.entities.Entity;

/**
 * Function which is used to compute a value on request from the given entity F.
 *
 * @param <F>
 *            Entity this computer is getting as input
 * @param <T>
 *            Type of the value this computer computes
 */
public interface Computer<F extends Entity, T> {
	public T compute(F f);
}
