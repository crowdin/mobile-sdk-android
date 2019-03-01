package com.crowdin.platform.repository.remote;

import android.content.Context;

import com.crowdin.platform.api.CrowdinApi;
import com.crowdin.platform.repository.LanguageDataCallback;

public class RemoteStringRepository implements RemoteRepository {

    private final CrowdinApi crowdinApi;

    public RemoteStringRepository(CrowdinApi crowdinApi) {
        this.crowdinApi = crowdinApi;
    }

    @Override
    public void fetchData(Context context, String language, LanguageDataCallback languageDataCallback) {
        // TODO: API call
//        new StringsLoaderTask(context, languageDataCallback).run();
    }
}
