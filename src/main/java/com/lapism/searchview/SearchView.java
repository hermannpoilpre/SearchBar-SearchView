package com.lapism.searchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.jetbrains.annotations.Contract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Kotlinize + NULLABLE
// todo konstruktory vsude + nullable
/*
todo
or a onFilterClickListener method is fine，thanks
sorry for my poor English，pardon me
*/
// this(context, null);
// @RestrictTo(LIBRARY_GROUP)
// @CoordinatorLayout.DefaultBehavior(SearchBehavior.class)
public class SearchView extends FrameLayout implements View.OnClickListener {

    public static final String TAG = SearchView.class.getName();

    private static int mIconColor = Color.BLACK;
    private static int mTextColor = Color.BLACK;
    private static int mTextHighlightColor = Color.BLACK;
    private static int mTextStyle = Typeface.NORMAL;
    private static Typeface mTextFont = Typeface.DEFAULT;

    private final Context mContext;
    private SearchArrowDrawable mSearchArrowDrawable;
    private View mViewShadow;
    private View mViewDivider;
    private View mMenuItemView;
    private CardView mCardView;
    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private FlexboxLayout mFlexboxLayout;
    private SearchEditText mSearchEditText;
    private List<Boolean> mSearchFiltersStates;
    private List<SearchFilter> mSearchFilters;



    private int mMenuItemCx = -1;

    private float mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_HAMBURGER;
    private boolean mArrow = false;
    private boolean mShadow = true;
    private CharSequence mQuery = "";


    // todo fix
    public void setTypeFace(){
        mSearchEditText.setTypeface();
    }

    private ImageView mImageViewNavigation;
    private ImageView mImageViewMic;
    private ImageView mImageViewMenu;

    private OnQueryTextListener mOnQueryTextListener;
    private OnOpenCloseListener mOnOpenCloseListener;
    private OnNavigationClickListener mOnNavigationClickListener;
    private OnMicClickListener mOnMicClickListener;
    private OnMenuClickListener mOnMenuClickListener;

    private boolean mGoogle = false;
    private int mVersion = Version.TOOLBAR;
    private int mVersionMargins = VersionMargins.TOOLBAR_SMALL;
    private int mTheme = Theme.LIGHT;
    // todo textstyle or typface

    private long mAnimationDuration; // kontrola

    // ---------------------------------------------------------------------------------------------
    public SearchView(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
        initStyle(null, 0, 0);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initStyle(attrs, 0, 0);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initStyle(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
        initStyle(attrs, defStyleAttr, defStyleRes);
    }

    // ---------------------------------------------------------------------------------------------
    @Contract(pure = true)
    @ColorInt
    public static int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(@ColorInt int color) {
        mIconColor = color;
        ColorFilter colorFilter = new PorterDuffColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);

        mImageViewNavigation.setColorFilter(colorFilter);
        mImageViewMic.setColorFilter(colorFilter);
        mImageViewMenu.setColorFilter(colorFilter);
    }

    @Contract(pure = true)
    @ColorInt
    public static int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mSearchEditText.setTextColor(mTextColor);
    }

            /*for (int i = 0, n = mFlexboxLayout.getChildCount(); i < n; i++) {
            View child = mFlexboxLayout.getChildAt(i);
            if (child instanceof AppCompatCheckBox) {
                ((AppCompatCheckBox) child).setTextColor(mTextColor);
            }
        }*/


    @Contract(pure = true)
    // todo
    public static Typeface getTextFont() {
        return mTextFont;
    }

    public void setTextFont(Typeface font) {
        mTextFont = font;
        mSearchEditText.setTypeface((Typeface.create(mTextFont, mTextStyle)));
    }

    @Contract(pure = true)
    @ColorInt
    public static int getTextHighlightColor() {
        return mTextHighlightColor;
    }

    public void setTextHighlightColor(@ColorInt int color) {
        mTextHighlightColor = color;
    }

    @Contract(pure = true)
    public static int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(int style) {
        mTextStyle = style;
        mSearchEditText.setTypeface((Typeface.create(mTextFont, mTextStyle)));
    }

    @Version
    public int getVersion() {
        return mVersion;
    }

    public void setVersion(@Version int version) {
        mVersion = version;

        if (mVersion == Version.MENU_ITEM) {
            setVisibility(View.GONE);
        }
    }

    public void setTheme(@Theme int theme, boolean tint) {
        mTheme = theme;

        switch (mTheme) {
            case Theme.LIGHT:
                setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_light_background));
                if (tint) {
                    setIconColor(ContextCompat.getColor(mContext, R.color.search_light_icon));
                    setHintColor(ContextCompat.getColor(mContext, R.color.search_light_hint));
                    setTextColor(ContextCompat.getColor(mContext, R.color.search_light_text));
                    setTextHighlightColor(ContextCompat.getColor(mContext, R.color.search_light_text_highlight));
                }
                break;
            case Theme.DARK:
                setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_dark_background));
                if (tint) {
                    setIconColor(ContextCompat.getColor(mContext, R.color.search_dark_icon));
                    setHintColor(ContextCompat.getColor(mContext, R.color.search_dark_hint));
                    setTextColor(ContextCompat.getColor(mContext, R.color.search_dark_text));
                    setTextHighlightColor(ContextCompat.getColor(mContext, R.color.search_dark_text_highlight));
                }
                break;
            case Theme.PLAY_STORE:
                setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_play_store_background));
                if (tint) {
                    setIconColor(ContextCompat.getColor(mContext, R.color.search_play_store_icon));
                    setHintColor(ContextCompat.getColor(mContext, R.color.search_play_store_hint));
                    setTextColor(ContextCompat.getColor(mContext, R.color.search_play_store_text));
                    setTextHighlightColor(ContextCompat.getColor(mContext, R.color.search_play_store_text_highlight));
                }
                break;
        }
    }

    @Theme
    public int getTheme() {
        return mTheme;
    }

    public void setTheme(@Theme int theme) {
        setTheme(theme, true);
    }



    public int getCustomHeight() {
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        return params.height;
    }

    public void setCustomHeight(int height) {
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        params.height = height;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mLinearLayout.setLayoutParams(params);
    }

    public void setQuery(CharSequence query, boolean submit) {
        mQuery = query;
        mSearchEditText.setText(query);
        mSearchEditText.setSelection(mSearchEditText.length());

        if (!TextUtils.isEmpty(mQuery)) {
            mImageViewMic.setVisibility(View.VISIBLE);
        } else {
            mImageViewMic.setVisibility(View.GONE);
        }

        if (submit) {
            onSubmitQuery();
        }
    }















    public void open(boolean animate) {
        open(animate, null);
    }

    public void open(boolean animate, MenuItem menuItem) {
        if (mVersion == Version.MENU_ITEM) {
            setVisibility(View.VISIBLE);

            if (animate) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (menuItem != null) {
                        getMenuItemPosition(menuItem.getItemId());
                    }
                    mCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                SearchAnimator.revealOpen(
                                        mCardView,
                                        mMenuItemCx,
                                        mAnimationDuration,
                                        mContext,
                                        mSearchEditText,
                                        mOnOpenCloseListener);
                            }
                        }
                    });
                } else {
                    SearchAnimator.fadeOpen(
                            mCardView,
                            mAnimationDuration,
                            mSearchEditText,
                            mOnOpenCloseListener);
                }
            } else {
                mCardView.setVisibility(View.VISIBLE);
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener.onOpen();
                }
                if (mSearchEditText.length() > 0) {
                    mSearchEditText.getText().clear();
                }
                mSearchEditText.requestFocus();
            }
        }

        if (mVersion == Version.TOOLBAR) {
            if (mSearchEditText.length() > 0) {
                mSearchEditText.getText().clear();
            }
            mSearchEditText.requestFocus();
        }
    }

    public void close(boolean animate) {
        close(animate, null);
    }

    public void close(boolean animate, MenuItem menuItem) {
        switch (mVersion) {
            case Version.MENU_ITEM:
                if (animate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (menuItem != null) {
                            getMenuItemPosition(menuItem.getItemId());
                        }
                        SearchAnimator.revealClose(
                                mCardView,
                                mMenuItemCx,
                                mAnimationDuration,
                                mContext,
                                mSearchEditText,
                                this,
                                mOnOpenCloseListener);
                    } else {
                        SearchAnimator.fadeClose(
                                mCardView,
                                mAnimationDuration,
                                mSearchEditText,
                                this,
                                mOnOpenCloseListener);
                    }
                } else {
                    if (mSearchEditText.length() > 0) {
                        mSearchEditText.getText().clear();
                    }
                    mSearchEditText.clearFocus();
                    mCardView.setVisibility(View.GONE);
                    setVisibility(View.GONE);
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onClose();
                    }
                }
                break;
            case Version.TOOLBAR:
                if (mSearchEditText.length() > 0) {
                    mSearchEditText.getText().clear();
                }
                mSearchEditText.clearFocus();
                break;
        }
    }

    public void setFilters(@Nullable List<SearchFilter> filters) {
        mSearchFilters = filters;
        mFlexboxLayout.removeAllViews();
        if (filters == null) {
            mSearchFiltersStates = null;
            mFlexboxLayout.setVisibility(View.GONE);
        } else {
            mSearchFiltersStates = new ArrayList<>();
            for (SearchFilter filter : filters) {
                AppCompatCheckBox checkBox = new AppCompatCheckBox(mContext);
                checkBox.setText(filter.getTitle());
                checkBox.setTextSize(12);
                checkBox.setTextColor(mTextColor);
                checkBox.setChecked(filter.isChecked());

                FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(getResources().getDimensionPixelSize(R.dimen.search_filter_margin_start), getResources().getDimensionPixelSize(R.dimen.search_filter_margin_top), getResources().getDimensionPixelSize(R.dimen.search_filter_margin_top), getResources().getDimensionPixelSize(R.dimen.search_filter_margin_top));

                checkBox.setLayoutParams(lp);
                checkBox.setTag(filter.getTagId());
                mFlexboxLayout.addView(checkBox);
                mSearchFiltersStates.add(filter.isChecked());
            }
        }
    }

    public List<SearchFilter> getSearchFilters() {
        if (mSearchFilters == null) {
            return new ArrayList<>();
        }

        dispatchFilters();

        List<SearchFilter> searchFilters = new ArrayList<>();
        for (SearchFilter filter : mSearchFilters) {
            searchFilters.add(new SearchFilter(filter.getTitle(), filter.isChecked(), filter.getTagId()));
        }

        return searchFilters;
    }

    public List<Boolean> getFiltersStates() {
        return mSearchFiltersStates;
    }

    public void addFocus() {
        if (mArrow) {
            mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_ARROW;
        } else {
            if (mSearchArrowDrawable != null) {
                mSearchArrowDrawable.setVerticalMirror(false);
                mSearchArrowDrawable.animate(SearchArrowDrawable.STATE_ARROW, mAnimationDuration);
                mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_ARROW;
            }
        }

        if (mShadow) {
            SearchAnimator.fadeIn(mViewShadow, mAnimationDuration);
        }

        if (!TextUtils.isEmpty(mQuery)) {
            mImageViewMic.setVisibility(View.VISIBLE);
        }

        showKeyboard();
        showSuggestions();

        if (mVersion == Version.TOOLBAR) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onOpen();
                    }
                }
            }, mAnimationDuration);
        }
    }

    public void removeFocus() {
        if (mArrow) {
            mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_HAMBURGER;
        } else {
            if (mSearchArrowDrawable != null) {
                mSearchArrowDrawable.setVerticalMirror(true);
                mSearchArrowDrawable.animate(SearchArrowDrawable.STATE_HAMBURGER, mAnimationDuration);
                mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_HAMBURGER;
            }
        }

        if (mShadow) {
            SearchAnimator.fadeOut(mViewShadow, mAnimationDuration);
        }

        if (TextUtils.isEmpty(mQuery)) {
            mImageViewMic.setVisibility(View.GONE);
            mImageViewMic.setVisibility(View.VISIBLE);
        }

        hideKeyboard();
        hideSuggestions();

        if (mVersion == Version.TOOLBAR) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onClose();
                    }
                }
            }, mAnimationDuration);
        }
    }

    public void showSuggestions() {
        if (mFlexboxLayout.getChildCount() > 0 && mFlexboxLayout.getVisibility() == View.GONE) {
            mViewDivider.setVisibility(View.VISIBLE);
            mFlexboxLayout.setVisibility(View.VISIBLE);
        }

        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter.getItemCount() > 0) {
            mViewDivider.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            SearchAnimator.fadeIn(mRecyclerView, mAnimationDuration);
        }
    }

    public void hideSuggestions() {
        if (mFlexboxLayout.getVisibility() == View.VISIBLE) {
            mViewDivider.setVisibility(View.GONE);
            mFlexboxLayout.setVisibility(View.GONE);
        }

        if (mRecyclerViewAdapter != null) {
            mViewDivider.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            SearchAnimator.fadeOut(mRecyclerView, mAnimationDuration);
        }
    }

    public void setShadowColor(@ColorInt int color) {
        mViewShadow.setBackgroundColor(color);
    }

    public void setGoogleIcons(boolean google) {
        mGoogle = google;

        if (mGoogle) {
            mImageViewNavigation.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_google_color_24dp));
            mImageViewMic.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_mic_color_24dp));
            // mImageViewMenu.setColorFilter(null);
        } else {
            mImageViewNavigation.setImageDrawable(mSearchArrowDrawable);
            mImageViewMic.setImageResource(R.drawable.ic_mic_black_24dp);
        }
    }

    //todo
    public void setRoundCorners(boolean roundCorners) {
        if (roundCorners) {
            //  mCardView.setRadius(getResources().getDimensionPixelSize(R.dimen.search_height));
            mCardView.setPreventCornerOverlap(false);
            mCardView.setBackgroundResource(R.drawable.round_background);
        } else {

        }
    }


    public void setTextSize(float size) {
        mSearchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }




    public void setNavigationIconAnimation(boolean animate) {
        if (!animate) {
            mSearchArrowDrawable.setProgress(SearchArrowDrawable.STATE_ARROW);
        }
        mArrow = !animate;
    }






    @Override
    public void setElevation(float elevation) {
        // todo
        mCardView.setMaxCardElevation(elevation);
        mCardView.setCardElevation(elevation);
    }

    public void setSuggestionsList(List<SearchItem> suggestionsList) {
        if (mRecyclerViewAdapter instanceof SearchAdapter) {
            ((SearchAdapter) mRecyclerViewAdapter).setSuggestionsList(suggestionsList);
        }
    }



    public boolean isOpen() {
        return getVisibility() == View.VISIBLE;
    }

    /*public boolean isShowingProgress() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }*/

    public void setShadow(boolean shadow) {
        if (shadow) {
            mViewShadow.setVisibility(View.VISIBLE);
        } else {
            mViewShadow.setVisibility(View.GONE);
        }
        mShadow = shadow;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }



    // ---------------------------------------------------------------------------------------------
    private void initView() {
        LayoutInflater.from(mContext).inflate((R.layout.search_view), this, true);

        /*final LayoutInflater inflater = LayoutInflater.from(context);
        final int layoutResId = a.getResourceId(R.styleable.SearchView_layout, R.layout.abc_search_view);
        inflater.inflate(layoutResId, this, true);*/

        mAnimationDuration = mContext.getResources().getInteger(R.integer.search_animation_duration);

        mSearchArrowDrawable = new SearchArrowDrawable(mContext);

        mViewDivider = findViewById(R.id.search_view_divider);
        mViewDivider.setVisibility(View.GONE);

        mViewShadow = findViewById(R.id.search_view_shadow);
        mViewShadow.setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_shadow_layout));
        mViewShadow.setOnClickListener(this);
        mViewShadow.setVisibility(View.GONE);

        mCardView = findViewById(R.id.search_cardView);
        mLinearLayout = findViewById(R.id.search_linearLayout);

        mImageViewNavigation = findViewById(R.id.search_imageView_navigation);
        mImageViewNavigation.setOnClickListener(this);

        mImageViewMic = findViewById(R.id.search_imageView_mic);
        mImageViewMic.setOnClickListener(this);

        mImageViewMenu = findViewById(R.id.search_imageView_menu);
        mImageViewMenu.setVisibility(View.GONE);

        mRecyclerView = findViewById(R.id.search_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mFlexboxLayout = findViewById(R.id.search_flexboxLayout);
        mFlexboxLayout.setVisibility(View.GONE);

        mSearchEditText = findViewById(R.id.search_searchEditText);
        mSearchEditText.setSearchView(this);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SearchView.this.onTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onSubmitQuery();
                return true;
            }
        });
        mSearchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    addFocus();
                } else {
                    removeFocus();
                }
            }
        });


        setVersion(Version.TOOLBAR);
        setGoogleIcons(true);
        setVersionMargins(VersionMargins.TOOLBAR_SMALL);
        setTheme(Theme.LIGHT, false);
        setRoundCorners(true);
    }

    // todo annotation
    private void initStyle(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray attr = mContext.obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, defStyleRes);
        if (attr != null) {
            if (attr.hasValue(R.styleable.SearchView_search_height)) {
                setCustomHeight(attr.getDimensionPixelSize(R.styleable.SearchView_search_height, mContext.getResources().getDimensionPixelSize(R.dimen.search_height)));
            }
            if (attr.hasValue(R.styleable.SearchView_search_version)) {
                setVersion(attr.getInt(R.styleable.SearchView_search_version, Version.TOOLBAR));
            }
            if (attr.hasValue(R.styleable.SearchView_search_version_margins)) {
                setVersionMargins(attr.getInt(R.styleable.SearchView_search_version_margins, VersionMargins.TOOLBAR_SMALL));
            }
            if (attr.hasValue(R.styleable.SearchView_search_theme)) {
                setTheme(attr.getInt(R.styleable.SearchView_search_theme, Theme.LIGHT));
            }
            if (attr.hasValue(R.styleable.SearchView_search_navigation_icon)) {
                setNavigationIcon(attr.getResourceId(R.styleable.SearchView_search_navigation_icon, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_icon_color)) {
                setIconColor(attr.getColor(R.styleable.SearchView_search_icon_color, Color.BLACK));
            }
            if (attr.hasValue(R.styleable.SearchView_search_background_color)) {
                setBackgroundColor(attr.getColor(R.styleable.SearchView_search_background_color, Color.WHITE));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_color)) {
                setTextColor(attr.getColor(R.styleable.SearchView_search_text_color, Color.BLACK));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_highlight_color)) {
                setTextHighlightColor(attr.getColor(R.styleable.SearchView_search_text_highlight_color, Color.GRAY));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_size)) {
                setTextSize(attr.getDimension(R.styleable.SearchView_search_text_size, mContext.getResources().getDimension(R.dimen.search_text_16)));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_style)) {
                setTextStyle(attr.getInt(R.styleable.SearchView_search_text_style, TextStyle.NORMAL));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint)) {
                setHint(attr.getString(R.styleable.SearchView_search_hint));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint_color)) {
                setHintColor(attr.getColor(R.styleable.SearchView_search_hint_color, Color.BLACK));
            }
            if (attr.hasValue(R.styleable.SearchView_search_animation_duration)) {
                setAnimationDuration(attr.getInteger(R.styleable.SearchView_search_animation_duration, mAnimationDuration));
            }
            if (attr.hasValue(R.styleable.SearchView_search_shadow)) {
                setShadow(attr.getBoolean(R.styleable.SearchView_search_shadow, true));
            }
            if (attr.hasValue(R.styleable.SearchView_search_shadow_color)) {
                setShadowColor(attr.getColor(R.styleable.SearchView_search_shadow_color, Color.TRANSPARENT));
            }
            if (attr.hasValue(R.styleable.SearchView_search_elevation)) {
                setElevation(attr.getDimensionPixelSize(R.styleable.SearchView_search_elevation, 0));
            }
            attr.recycle();
        }
    }

    private void restoreFiltersState(List<Boolean> states) {
        mSearchFiltersStates = states;
        for (int i = 0, j = 0, n = mFlexboxLayout.getChildCount(); i < n; i++) {
            View view = mFlexboxLayout.getChildAt(i);
            if (view instanceof AppCompatCheckBox) {
                ((AppCompatCheckBox) view).setChecked(mSearchFiltersStates.get(j++));
            }
        }
    }

    private void getMenuItemPosition(int menuItemId) {
        if (mMenuItemView != null) {
            mMenuItemCx = getCenterX(mMenuItemView);
        }
        ViewParent viewParent = getParent();
        while (viewParent != null && viewParent instanceof View) {
            View parent = (View) viewParent;
            View view = parent.findViewById(menuItemId);
            if (view != null) {
                mMenuItemView = view;
                mMenuItemCx = getCenterX(mMenuItemView);
                break;
            }
            viewParent = viewParent.getParent();
        }
    }

    private int getCenterX(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0] + view.getWidth() / 2;
    }

    private void isVoiceAvailable() {
        if (isInEditMode()) {
            return;//break continue
        }
    }

    private void onTextChanged(CharSequence newText) {
        mQuery = newText;

        if (mRecyclerViewAdapter != null && mRecyclerViewAdapter instanceof Filterable) {
            final CharSequence mFilterKey = newText.toString().toLowerCase(Locale.getDefault());
            ((SearchAdapter) mRecyclerViewAdapter).getFilter().filter(newText, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int i) {
                    if (mFilterKey.equals(((SearchAdapter) mRecyclerViewAdapter).getKey())) {
                        if (i > 0) {
                            if (mRecyclerView.getVisibility() == View.GONE) {
                                mViewDivider.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                SearchAnimator.fadeIn(mRecyclerView, mAnimationDuration);
                            }
                        } else {
                            if (mRecyclerView.getVisibility() == View.VISIBLE) {
                                mViewDivider.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.GONE);
                                SearchAnimator.fadeOut(mRecyclerView, mAnimationDuration);
                            }
                        }
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(newText)) {
            mImageViewMic.setImageResource(R.drawable.ic_clear_black_24dp);
        } else {
            if (mGoogle) {
                mImageViewMic.setImageResource(R.drawable.ic_mic_color_24dp);
            } else {
                mImageViewMic.setImageResource(R.drawable.ic_mic_black_24dp);
                // TODO BARVY
            }
        }

        if (mOnQueryTextListener != null) {
            dispatchFilters();
            mOnQueryTextListener.onQueryTextChange(newText.toString());
        }
    }

    private void onSubmitQuery() {
        CharSequence query = mSearchEditText.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            dispatchFilters();
            if (mOnQueryTextListener == null || !mOnQueryTextListener.onQueryTextSubmit(query.toString())) {
                mSearchEditText.setText(query);
            }
        }
    }

    private void dispatchFilters() {
        if (mSearchFiltersStates != null) {
            for (int i = 0, j = 0, n = mFlexboxLayout.getChildCount(); i < n; i++) {
                View view = mFlexboxLayout.getChildAt(i);
                if (view instanceof AppCompatCheckBox) {
                    boolean isChecked = ((AppCompatCheckBox) view).isChecked();
                    mSearchFiltersStates.set(j, isChecked);
                    mSearchFilters.get(j).setChecked(isChecked);
                    j++;
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        // int id = view.getId();

        /*switch (id){
            case mImageViewMenu
        }*/

        if (view == mImageViewNavigation) {
            if (mSearchArrowDrawable != null && mIsSearchArrowHamburgerState == SearchArrowDrawable.STATE_ARROW) {
                close(true);
            } else {
                if (mOnNavigationClickListener != null) {
                    mOnNavigationClickListener.onNavigationClick(mIsSearchArrowHamburgerState);
                }
            }
        }

        if (view == mImageViewMic) {
            if (mOnMicClickListener != null) {
                mOnMicClickListener.onMicClick();
            }
            if (mSearchEditText.length() > 0) {
                mSearchEditText.getText().clear();
            }
        }

        if (view == mImageViewMenu) {
            if (mOnNavigationClickListener != null) {
                mOnNavigationClickListener.onNavigationClick(mIsSearchArrowHamburgerState);
            }
        }

        if (view == mViewShadow) {
            close(true);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());

        bundle.putCharSequence("query", mQuery);
        bundle.putBoolean("isSearchOpen", getVisibility() == View.VISIBLE);

        dispatchFilters();
        ArrayList<Integer> searchFiltersStatesInt = null;
        if (mSearchFiltersStates != null) {
            searchFiltersStatesInt = new ArrayList<>();
            for (Boolean bool : mSearchFiltersStates) {
                searchFiltersStatesInt.add((bool) ? 1 : 0);
            }
        }
        bundle.putIntegerArrayList("searchFiltersStates", searchFiltersStatesInt);

        ArrayList<SearchFilter> searchFilters = null;
        if (mSearchFilters != null) {
            searchFilters = new ArrayList<>();
            searchFilters.addAll(mSearchFilters);
        }
        bundle.putParcelableArrayList("searchFilters", searchFilters);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mQuery = bundle.getCharSequence("query");
            if (bundle.getBoolean("isSearchOpen")) {
                open(true);
                setQuery(mQuery, false);
                mSearchEditText.requestFocus();
            }

            ArrayList<Integer> searchFiltersStatesInt = bundle.getIntegerArrayList("searchFiltersStates");
            ArrayList<Boolean> searchFiltersStatesBool = null;
            if (searchFiltersStatesInt != null) {
                searchFiltersStatesBool = new ArrayList<>();
                for (Integer value : searchFiltersStatesInt) {
                    searchFiltersStatesBool.add(value == 1);
                }
            }
            restoreFiltersState(searchFiltersStatesBool);

            mSearchFilters = bundle.getParcelableArrayList("searchFilters");

            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }























    // ---------------------------------------------------------------------------------------------
    public void showKeyboard() {
        if (!isInEditMode()) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(mSearchEditText, 0);
                inputMethodManager.showSoftInput(this, 0);
            }
        }
    }

    public void hideKeyboard() {
        if (!isInEditMode()) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @VersionMargins
    public int getVersionMargins() {
        return mVersionMargins;
    }

    public void setVersionMargins(@VersionMargins int versionMargins) {
        mVersionMargins = versionMargins;

        if (mVersionMargins == VersionMargins.TOOLBAR_SMALL) {
            int top = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_top);
            int leftRight = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_small_left_right);
            int bottom = 0;

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(leftRight, top, leftRight, bottom);

            mCardView.setLayoutParams(params);
        }

        if (mVersionMargins == VersionMargins.TOOLBAR_BIG) {
            int top = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_top);
            int leftRight = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_big_left_right);
            int bottom = 0;

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(leftRight, top, leftRight, bottom);

            mCardView.setLayoutParams(params);
        }

        if (mVersionMargins == VersionMargins.MENU_ITEM) {
            int top = mContext.getResources().getDimensionPixelSize(R.dimen.search_menu_item_margin);
            int leftRight = mContext.getResources().getDimensionPixelSize(R.dimen.search_menu_item_margin_left_right);
            int bottom = mContext.getResources().getDimensionPixelSize(R.dimen.search_menu_item_margin);

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(leftRight, top, leftRight, bottom);

            mCardView.setLayoutParams(params);
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mCardView.setCardBackgroundColor(color);
    }

    public void setHintColor(@ColorInt int color) {
        mSearchEditText.setHintTextColor(color);
    }

    public Editable getText() {
        return mSearchEditText.getText();
    }

    public void setText(CharSequence text) {
        mSearchEditText.setText(text);
    }

    public void setText(@StringRes int text) {
        mSearchEditText.setText(text);
    }

    public void setHint(CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    public void setHint(@StringRes int hint) {
        mSearchEditText.setHint(hint);
    }

    public void setImeOptions(int imeOptions) {
        mSearchEditText.setImeOptions(imeOptions);
    }

    public void setInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }

    public void setNavigationIcon(@DrawableRes int resource) {
        mImageViewNavigation.setImageResource(resource);
    }

    public void setNavigationIcon(@Nullable Drawable drawable) {
        mImageViewNavigation.setImageDrawable(drawable);
    }

    public void setMenuIcon(@DrawableRes int resource) {
        mImageViewMenu.setImageResource(resource);
    }

    public void setMenuIcon(@Nullable Drawable drawable) {
        mImageViewMenu.setImageDrawable(drawable);
    }

    public void setMicIcon(@DrawableRes int resource) {
        mImageViewMic.setImageResource(resource);
    }

    public void setMicIcon(@Nullable Drawable drawable) {
        mImageViewMic.setImageDrawable(drawable);
    }

    public void setSearchItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerViewAdapter = adapter;
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    /**
     * new SearchDivider(Context)
     * new DividerItemDecoration(Context, DividerItemDecoration.VERTICAL)
     */
    public void addDivider(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * new SearchDivider(Context)
     * new DividerItemDecoration(Context, DividerItemDecoration.VERTICAL)
     */
    public void removeDivider(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.removeItemDecoration(itemDecoration);
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryTextListener = listener;
    }

    public void setOnOpenCloseListener(OnOpenCloseListener listener) {
        mOnOpenCloseListener = listener;
    }

    public void setOnNavigationClickListener(OnNavigationClickListener listener) {
        mOnNavigationClickListener = listener;
    }

    public void setOnMicClickListener(OnMicClickListener listener) {
        mOnMicClickListener = listener;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        if(listener != null) {
            mImageViewMenu.setImageResource(R.drawable.ic_menu_black_24dp);
            mImageViewMenu.setOnClickListener(this);
            mImageViewMenu.setVisibility(View.VISIBLE);
        }

        mOnMenuClickListener = listener;
    }

    // ---------------------------------------------------------------------------------------------
    @IntDef({Version.TOOLBAR, Version.MENU_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Version {
        int TOOLBAR = 1000;
        int MENU_ITEM = 1001;
    }

    @IntDef({VersionMargins.TOOLBAR_SMALL, VersionMargins.TOOLBAR_BIG, VersionMargins.MENU_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VersionMargins {
        int TOOLBAR_SMALL = 2000;
        int TOOLBAR_BIG = 2001;
        int MENU_ITEM = 2002;
    }

    @IntDef({Theme.LIGHT, Theme.DARK, Theme.PLAY_STORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Theme {
        int LIGHT = 3000;
        int DARK = 3001;
        int PLAY_STORE = 3002;
    }

    @IntDef({TextStyle.NORMAL, TextStyle.BOLD, TextStyle.ITALIC, TextStyle.BOLD_ITALIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextStyle {
        int NORMAL = Typeface.NORMAL;
        int BOLD = Typeface.BOLD;
        int ITALIC = Typeface.ITALIC;
        int BOLD_ITALIC = Typeface.BOLD_ITALIC;
    }

    // ---------------------------------------------------------------------------------------------
    public interface OnQueryTextListener {
        boolean onQueryTextChange(String newText);

        boolean onQueryTextSubmit(String query);
    }

    public interface OnOpenCloseListener {
        boolean onClose();

        boolean onOpen();
    }

    public interface OnNavigationClickListener {
        void onNavigationClick(float state);
    }

    public interface OnMicClickListener {
        void onMicClick();
    }

    public interface OnMenuClickListener {
        void onMenuClick();
    }

}
