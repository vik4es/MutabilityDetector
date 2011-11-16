/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.unittesting.matchers.reasons;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedMutableReasonDetails;
import static org.mutabilitydetector.TestUtil.unusedReason;
import static org.mutabilitydetector.unittesting.matchers.reasons.NoReasonsAllowedMatcher.noWarningsAllowed;
import static org.mutabilitydetector.unittesting.matchers.reasons.WithAllowedReasonsMatcher.withAllowedReasons;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.unittesting.matchers.IsImmutableMatcher;

@SuppressWarnings("unchecked")
public class WithAllowedReasonsMatcherTest {

    CodeLocation<?> unusedCodeLocation = TestUtil.unusedCodeLocation();

    @Test
    public void passesWhenPrimaryResultPasses() throws Exception {
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = definitelyImmutable("some.class");

        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(noWarningsAllowed()));

        assertThat(withReasonsMatcher.matches(analysisResult), is(true));
    }

    @Test
    public void failsWhenPrimaryResultFailsAndNoReasonsAreAllowed() throws Exception {
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, unusedMutableReasonDetails());
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(noWarningsAllowed()));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(false));
    }
    
    @Test public void passesWhenResultDoesNotMatchButTheOffendingReasonsAreAllowed() {
        MutableReasonDetail anyReason = TestUtil.unusedMutableReasonDetail(); 
        
        Matcher<MutableReasonDetail> allowWhateverReason = mock(Matcher.class);
        when(allowWhateverReason.matches(anyReason)).thenReturn(true);
        
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, anyReason);
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(allowWhateverReason));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(true));
    }
    
    @Test public void failsWhenResultDoesNotMatchAndOnlyOneOfManyReasonsAreAllowed() {
        MutableReasonDetail allowedReason = newMutableReasonDetail("allowed", unusedCodeLocation, unusedReason());
        MutableReasonDetail disallowedReason = newMutableReasonDetail("disallowed", unusedCodeLocation, unusedReason());
        
        Matcher<MutableReasonDetail> onlyAllowOneReason = mock(Matcher.class);
        when(onlyAllowOneReason.matches(allowedReason)).thenReturn(true);
        when(onlyAllowOneReason.matches(disallowedReason)).thenReturn(false);
        
        IsImmutableMatcher isImmutable = IsImmutableMatcher.hasIsImmutableStatusOf(IMMUTABLE);
        AnalysisResult analysisResult = analysisResult("some class", NOT_IMMUTABLE, asList(allowedReason, disallowedReason));
        
        WithAllowedReasonsMatcher withReasonsMatcher = withAllowedReasons(isImmutable, singleton(onlyAllowOneReason));
        
        assertThat(withReasonsMatcher.matches(analysisResult), is(false));
    }

}
