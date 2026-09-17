---
name: bump-version
description: Use when releasing a new version of the Crowdin Android SDK (mobile-sdk-android), e.g. "bump version to 1.19.0", "prepare release", or a "chore: version X.Y.Z" PR.
---

# Bump Version

Reference: PR #378 (`chore: version 1.18.3`). `NEW` = version being released,
`PREV` = current `crowdinVersion` in `gradle.properties` (the latest published release).

## Steps

1. `gradle.properties`: `crowdinVersion=NEW`, `libraryVersionCode` += 1 (always exactly +1).
2. Docs: replace `mobile-sdk-android:sdk:PREV` and `mobile-sdk-android:controls:PREV` with `NEW` in
   - `website/docs/installation.md` (two snippets)
   - `website/docs/guides/screenshots-automation.md`
   - `website/docs/advanced-features/sdk-controls.mdx`

   Then `grep -rn PREV --exclude-dir=node_modules .` to catch new snippets. Do not touch prose like
   "Available starting from Crowdin SDK version 1.18.0" or `package-lock.json` matches.
3. `crowdinSdk` pin in `gradle/libs.versions.toml`: apply the rule below.
4. Commit `chore: version NEW` on branch `chore/version-NEW` (create or check out). PR title is the
   same, base `master`. The `NEW` git tag is created after merge, not in the PR.

## The crowdin-controls Pin Rule

`crowdin-controls` depends on the *published* JitPack artifact `com.github.crowdin.mobile-sdk-android:sdk`
(`crowdinSdk` in `gradle/libs.versions.toml`), not on `project(":crowdin")`. JitPack builds from tags,
so `NEW` does not exist yet when the PR is opened.

**Pin `crowdinSdk` to PREV, i.e. one release behind `crowdinVersion`.** Never to `NEW`: CI's
`./gradlew build` would fail on `:crowdin-controls`.

Before moving the pin, check PREV's POM is consumable:

```bash
curl -sL https://jitpack.io/com/github/crowdin/mobile-sdk-android/sdk/PREV/sdk-PREV.pom | grep -E '<project|<version></version>|<version/>'
```

- `<project` line present and no empty `<version>` lines: set `crowdinSdk = "PREV"` and trim the
  "Keep at ... for now" comment above it.
- Empty `<version>` lines present (the 1.18.x Compose BOM problem, from `crowdin/maven.gradle`
  writing `it.version` for BOM-managed deps): keep the current pin, extend the comment's version
  list, and put in the PR description: "crowdinSdk stays at X: PREV POM has unversioned Compose
  deps; this release ships the same POM until the maven.gradle fix lands."
- No `<project` line: fetch failed (offline or JitPack still building). Retry or keep the pin.

Optional verification when the pin moved: `./gradlew :crowdin-controls:dependencies --configuration releaseCompileClasspath`.

## Not Touched

`crowdin-controls/build.gradle` own `libraryVersionName` (1.0.x) and `libraryVersionCode`;
`crowdin/build.gradle`, `crowdin-gradle-plugin`, `crowdin-compiler-plugin` (they read `crowdinVersion`).
