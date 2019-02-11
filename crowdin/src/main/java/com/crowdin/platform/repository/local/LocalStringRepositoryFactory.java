package com.crowdin.platform.repository.local;

import android.content.Context;

import com.crowdin.platform.CrowdinConfig;

public class LocalStringRepositoryFactory {

    private LocalStringRepositoryFactory() {
    }

    public static LocalRepository createLocalRepository(Context context, CrowdinConfig config) {
        LocalRepository localRepository;
        if (config.isPersist()) {
            localRepository = new SharedPrefLocalRepository(context);
        } else {
            localRepository = new MemoryLocalRepository();
        }

        return localRepository;
    }
}
