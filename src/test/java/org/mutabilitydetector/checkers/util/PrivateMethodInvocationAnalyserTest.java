package org.mutabilitydetector.checkers.util;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mutabilitydetector.TestUtil;

public class PrivateMethodInvocationAnalyserTest {

    @Test
    public void testCanQueryIfAMethodIsOnlyCalledFromTheConstructor() throws Exception {
        PrivateMethodInvocationAnalyser analyser = new PrivateMethodInvocationAnalyser();
        TestUtil.retrieveInformation(analyser, PrivateMethodsCalledOnlyInConstructor.class);

        String methodDescriptor = "privateMethod:()V";
        boolean result = analyser.isPrivateMethodCalledOnlyFromConstructor(methodDescriptor);
        assertTrue("Should report private method is only called from constructor.", result);
    }

    @Test
    public void isPrivateMethodCalledOnlyFromConstructorReturnsFalseWhenPrivateMethodIsInvokedInPublicMethod()
            throws Exception {
        PrivateMethodInvocationAnalyser analyser = new PrivateMethodInvocationAnalyser();
        TestUtil.retrieveInformation(analyser, PrivateMethodsCalledOutsideConstructor.class);

        String methodDescriptor = "privateMethod:()V";
        boolean result = analyser.isPrivateMethodCalledOnlyFromConstructor(methodDescriptor);
        assertFalse("Should report private method is only called from constructor.", result);
    }

    private static class PrivateMethodsCalledOnlyInConstructor {
        @SuppressWarnings("unused")
        public PrivateMethodsCalledOnlyInConstructor() {
            privateMethod();
        }

        private void privateMethod() {
        }
    }

    @SuppressWarnings("unused")
    private static class PrivateMethodsCalledOutsideConstructor {
        private void privateMethod() {
        }

        public void callsThePrivateMethod() {
            privateMethod();
        }
    }
}
