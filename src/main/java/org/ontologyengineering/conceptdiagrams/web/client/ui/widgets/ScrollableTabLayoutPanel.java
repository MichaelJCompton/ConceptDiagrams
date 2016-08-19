package org.ontologyengineering.conceptdiagrams.web.client.ui.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.ontologyengineering.conceptdiagrams.web.client.ui.InterfaceGlobals;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */


/**
 * FIXME : didn't finish this at the moment - it's not essential and requires a bit of tinkering to get correct,
 * so at the moment this isn't used.  Instead I just have an add button and let the tabs extend past the right of the screen.
 */

public class ScrollableTabLayoutPanel extends TabLayoutPanel {

    // based on http://gwtsnippets.blogspot.com.au/2015/01/tablayoutpanel-with-scrolling.html
    // Also as a + button



    private static final int PADDING_PIXELS = 4;
    private static final int SCROLL_PIXELS = 50;

    private LayoutPanel panel;
    private FlowPanel tabBar;
    private FlowPanel scrollButtonPanel = null;
    private Image scrollLeftButton;
    private Image scrollRightButton;
    private HandlerRegistration windowResizeHandler;

    private boolean hasAddTab = false;

    public ScrollableTabLayoutPanel(double barHeight, Style.Unit barUnit) {
        super(barHeight, barUnit);

        scrollLeftButton = new Image(InterfaceGlobals.INSTANCE.getIconImages().leftArrow());
        scrollRightButton = new Image(InterfaceGlobals.INSTANCE.getIconImages().rightArrow());

        // The main widget wrapped by this composite, which is a LayoutPanel
        // with the tab bar & the tab content
        panel = (LayoutPanel) getWidget();

        // Find the tab bar, which is the first flow panel in the LayoutPanel
        for (int i = 0; i < panel.getWidgetCount(); ++i) {
            Widget widget = panel.getWidget(i);
            if (widget instanceof FlowPanel) {
                tabBar = (FlowPanel) widget;
                break; // tab bar found
            }
        }
    }


    public void addAddTab(ClickHandler ch) {
        if(!hasAddTab) {
            final HTMLPanel hPanel = new HTMLPanel("");

            Image addButton = new Image(InterfaceGlobals.INSTANCE.getIconImages().add());
            addButton.setSize("13px", "13px");
            addButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            addButton.addClickHandler(ch);

            hPanel.add(addButton);
            //closeButton.getElement().getStyle().setMarginTop(0.2, Style.Unit.EM);

            super.add(new Label(), addButton);
        }
        hasAddTab = true;
    }



    @Override
    public void add(Widget child, Widget tab) {
        if(hasAddTab) {
            super.insert(child, tab, getWidgetCount() - 1);
            selectTab(getWidgetCount() - 2);
        } else {
            super.add(child, tab);
        }
        checkIfScrollButtonsNecessary();
    }

    @Override
    public void selectTab(int index) {
        super.selectTab(index);
        scrollToSelected();
    }

    @Override
    public boolean remove(int index) {
        boolean b = super.remove(index);
        checkIfScrollButtonsNecessary();
        scrollToSelected();
        return b;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        if (windowResizeHandler == null) {
            windowResizeHandler = Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    checkIfScrollButtonsNecessary();
                }
            });
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        if (windowResizeHandler != null) {
            windowResizeHandler.removeHandler();
            windowResizeHandler = null;
        }
    }

    private void scrollToSelected() {

        int tabBarWidth = getTabBarWidth();
        int rightOfSelected = getRightOfWidget(getTabWidget(getSelectedIndex())
                .getParent());

        scrollTo(Math.min(0, tabBarWidth - rightOfSelected));
    }

    private ClickHandler createScrollClickHandler(final int diff) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Widget lastTab = getLastTab();
                if (lastTab == null)
                    return;

                int newLeft = parsePosition(tabBar.getElement().getStyle()
                        .getLeft())
                        + diff;
                int rightOfLastTab = getRightOfWidget(lastTab);

                // Do not scroll too far to the left
                if (newLeft > 0)
                    newLeft = 0;

                // And not too far to the right either
                if (getTabBarWidth() - newLeft > rightOfLastTab) {
                    newLeft = getTabBarWidth() - rightOfLastTab;
                }

                scrollTo(newLeft);
            }
        };
    }

    /** Create and attach the scroll button images with a click handler */
    private void initScrollButtonPanel() {

        // Only initialize if not already done
        if (scrollButtonPanel == null) {

            // Initialize flow panel -
            // height and width to fit tabs and buttons
            scrollButtonPanel = new FlowPanel();
            int tabHeight = tabBar.getElement().getFirstChildElement()
                    .getClientHeight();
            scrollButtonPanel.setHeight(tabHeight + "px");
            scrollButtonPanel.setWidth(tabHeight * 2 + "px");
            scrollButtonPanel.setVisible(false);

            // Left button
            scrollButtonPanel.add(scrollLeftButton);

            // Right button
            scrollButtonPanel.add(scrollRightButton);

            // Click handlers
            scrollRightButton.addClickHandler(createScrollClickHandler(-1
                    * SCROLL_PIXELS));
            scrollLeftButton
                    .addClickHandler(createScrollClickHandler(SCROLL_PIXELS));

            // Styles
            //scrollRightButton.setStyleName("tabPanelScrollButton");
            //scrollLeftButton.setStyleName("tabPanelScrollButton");

            // Insert scroll buttons in tablayoutpanel
            panel.insert(scrollButtonPanel, 0);
            panel.setWidgetRightWidth(scrollButtonPanel, 0, Style.Unit.EM,
                    tabHeight * 2, Style.Unit.EM);
        }
    }

    private void checkIfScrollButtonsNecessary() {
        // Defer size calculations until sizes are available, when calculating
        // immediately after
        // add(), all size methods return zero

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {

                initScrollButtonPanel();

                boolean isScrolling = isScrollingNecessary();
                // When the scroll buttons are being hidden, reset the scroll
                // position to zero to
                // make sure no tabs are still out of sight
                if (scrollButtonPanel.isVisible() && !isScrolling) {
                    resetScrollPosition();
                }

                // Set visible or hide
                // Must be done before changing tabbar size if isScrolling
                scrollButtonPanel.setVisible(isScrolling);

                // If scrolling necessary then make room for scroll buttons
                if (isScrolling) {
                    tabBar.getElement()
                            .getParentElement()
                            .getStyle()
                            .setRight(
                                    scrollButtonPanel.getOffsetWidth()
                                            + PADDING_PIXELS, Style.Unit.EM);
                }
                scrollToSelected();
            }
        });

    }

    private void resetScrollPosition() {
        scrollTo(0);
    }

    private void scrollTo(int pos) {
        tabBar.getElement().getStyle().setLeft(pos, Style.Unit.EM);
    }

    private boolean isScrollingNecessary() {
        Widget lastTab = getLastTab();
        if (lastTab == null)
            return false;

        return getRightOfWidget(lastTab) > getTabBarWidth();
    }

    private int getRightOfWidget(Widget widget) {
        return widget.getElement().getOffsetLeft()
                + widget.getElement().getOffsetWidth();
    }

    private int getTabBarWidth() {
        return tabBar.getElement().getParentElement().getClientWidth();
    }

    private Widget getLastTab() {
        if (tabBar.getWidgetCount() == 0)
            return null;

        return tabBar.getWidget(tabBar.getWidgetCount() - 1);
    }

    private static int parsePosition(String positionString) {
        int position;
        try {
            for (int i = 0; i < positionString.length(); i++) {
                char c = positionString.charAt(i);
                if (c != '-' && !(c >= '0' && c <= '9')) {
                    positionString = positionString.substring(0, i);
                }
            }

            position = Integer.parseInt(positionString);
        } catch (NumberFormatException ex) {
            position = 0;
        }
        return position;
    }

}
