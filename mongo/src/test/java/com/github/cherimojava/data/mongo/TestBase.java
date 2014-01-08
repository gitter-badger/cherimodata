/**
 *    Copyright [cherimojava (http://github.com/cherimojava/cherimodata.git)]
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
 */
package com.github.cherimojava.data.mongo;

import org.hamcrest.Matcher;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

/**
 * Base Class for Simple Tests not requiring MongoDB access
 *
 * @author philnate
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public abstract class TestBase {

	/**
	 * just some little tool method to avoid that JSONAssert requires the TestCase method to throw an exception
	 *
	 * @param expected
	 * @param actual
	 * @return
	 */
	public void assertJson(Matcher<? super String> expected, String actual) {
		assertThat(actual, expected);
	}
}