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

/**
 *
 * Functional Interface designating classes capable of creating objects
 *
 * @author philnate
 * @param <S>
 *            Input object used to generate O from
 * @param <O>
 *            Output Object generated from S
 */
public interface Factory<S, O> {

	/**
	 * Create O from input
	 *
	 * @param input
	 * @return
	 */
	public O create(S input);
}
