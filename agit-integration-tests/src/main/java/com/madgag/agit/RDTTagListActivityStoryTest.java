/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.madgag.agit.git.model.RDTTag;
import com.madgag.agit.git.model.RDTTag.TagSummary;
import com.madgag.agit.matchers.GitTestHelper;

import java.util.List;

import org.eclipse.jgit.lib.Repository;

public class RDTTagListActivityStoryTest extends RDTTypeListActivityStoryTestBase<RDTypeListActivity> {

    private final static String TAG = RDTTagListActivityStoryTest.class.getSimpleName();

    public RDTTagListActivityStoryTest() {
        super("com.madgag.agit", RDTypeListActivity.class);
    }

    public void testShouldShowAllTags() throws Exception {

        GitTestHelper helper = AndroidTestEnvironment.helper(getInstrumentation());
        Repository repoWithTags = helper.unpackRepo("small-repo.with-tags.zap");

        setActivityIntent(listIntent(repoWithTags, "tag"));

        final RDTypeListActivity activity = getActivity();

        ListView listView = activity.getListView();

        checkCanSelectEveryItemInNonEmpty(listView);

        RDTTag tagDomainType = new RDTTag(repoWithTags);
        List<TagSummary> summaries = tagDomainType.getAll();
        Log.i(TAG, "Should be " + summaries.size() + " elements in the list.. there are " + listView.getCount());
        assertThat(listView.getCount(), is(summaries.size()));
        for (int index = 0; index < summaries.size(); ++index) {
            TagSummary summary = summaries.get(index);
            View itemView = getItemViewBySelecting(listView, index);
            Log.d(TAG, "summary=" + summary + " view=" + itemView);
            TextView itemTitleTextView = (TextView) itemView.findViewById(android.R.id.text1);
            assertThat(itemTitleTextView.getText(), is(summary.getName()));

            if (summary.getName().equals("annotated-tag-of-2nd-commit")) {
                CharSequence dt = ((TextView) itemView.findViewById(android.R.id.text2)).getText();
                Log.i(TAG, "Looking... " + dt);
                assertThat(dt, charSequence(startsWith("Commit")));
                assertThat(dt, charSequence(containsString("Adding my happy folder with it's tags")));
            }
        }
    }

}
