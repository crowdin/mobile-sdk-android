@NonNull
@Override
public AppCompatDelegate getDelegate() {
    return new BaseContextWrappingDelegate(super.getDelegate());
}