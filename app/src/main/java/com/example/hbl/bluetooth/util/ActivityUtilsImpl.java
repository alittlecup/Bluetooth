package com.example.hbl.bluetooth.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.annimon.stream.Optional;
import com.example.hbl.bluetooth.interfaces.BackPropagatingFragment;
import com.example.hbl.bluetooth.login.LoginFragment;

import java.util.List;

import rx.functions.Func1;


public final class ActivityUtilsImpl implements ActivityUtils {

    private final CollectionUtils collectionUtils;

    public ActivityUtilsImpl(final CollectionUtils collectionUtils) {
        this.collectionUtils = collectionUtils;
    }

    @Override
    public void addFragmentToActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment, final int frameId) {
        if (!fragment.isAdded()) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(frameId, fragment);
            transaction.commit();
        }
    }

    @Override
    public void addFragmentWithTagToActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment, final int frameId, @NonNull final String tag) {
        if (!fragment.isAdded()) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(frameId, fragment, tag);
            transaction.commit();
        }
    }

    @Override
    public void addFragmentWithTagToActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment, final String tag, final int frameId,
                                             final String backStackName) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment, tag);
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

    @Override
    public void setFragmentWithTagToActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment, final String tag, final int frameId,
                                             final String backStackName) {
        setFragmentWithTagToActivity(fragmentManager, fragment, tag, frameId, backStackName, true);
    }

    @Override
    public void setFragmentWithTagToActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment, final String tag, final int frameId,
                                             final String backStackName, final boolean animateEntrance) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, tag);
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

    @Override
    public void removeFragmentFromActivity(@NonNull final FragmentManager fragmentManager, @NonNull final Fragment fragment) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    @Override
    public void removeFragmentWithTagFromActivity(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        final Fragment fragment = fragmentManager.findFragmentByTag(LoginFragment.TAG);
        if (fragment != null) {
            removeFragmentFromActivity(fragmentManager,fragment);
        }
    }

    /**
     * @return {@code true} is back is consumed, {@code false} otherwise
     */
    @Override
    public boolean propagateBackToTopFragment(@NonNull final FragmentManager fragmentManager) {
        return callIfPresent(findBackPropagatingFragment(fragmentManager), BackPropagatingFragment::onBack);
    }

    private boolean callIfPresent(final Optional<BackPropagatingFragment> backPropagatingFragmentOptional, final Func1<BackPropagatingFragment, Boolean> action) {
        if (backPropagatingFragmentOptional.isPresent()) {
            final BackPropagatingFragment backPropagatingFragment = backPropagatingFragmentOptional.get();
            return action.call(backPropagatingFragment);
        }
        return false;
    }

    private Optional<BackPropagatingFragment> findBackPropagatingFragment(final FragmentManager fragmentManager) {
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (collectionUtils.isEmpty(fragments)) {
            return Optional.empty();
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            final Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isVisible()) {
                if (fragment instanceof BackPropagatingFragment) {
                    return Optional.of(((BackPropagatingFragment) fragment));
                }
                break;
            }
        }
        return Optional.empty();
    }
}
