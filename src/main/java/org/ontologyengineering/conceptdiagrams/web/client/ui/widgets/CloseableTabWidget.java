package org.ontologyengineering.conceptdiagrams.web.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import org.ontologyengineering.conceptdiagrams.web.client.ui.InterfaceGlobals;
import org.ontologyengineering.conceptdiagrams.web.client.ui.MenuIcons;


/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */

/**
 * FIXME: Depricated for the moment.  I think to do this really well would need styling etc and I would want to
 * have something like a DecoratedTabWidget to handle clost and add etc, where one could layout some number of
 * text and widgets.  But for the moment it's probably best to just make a purpose fit panel in LienzoDiagramCanvas
 */
public class CloseableTabWidget extends Composite implements HasCloseHandlers<CloseableTabWidget> {

    // based on http://gwtsnippets.blogspot.com.au/2015/01/tablayoutpanel-with-close-buttons.html



    @UiConstructor
    public CloseableTabWidget(final String label) {
        final HTMLPanel hPanel = new HTMLPanel("");

        final HTML html = new HTML(SafeHtmlUtils.fromString(label));
        html.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        html.getElement().getStyle().setFloat(Style.Float.LEFT);
        hPanel.add(html);

        Image closeButton = new Image(InterfaceGlobals.INSTANCE.getIconImages().close());
        closeButton.setSize("13px", "13px");
        closeButton.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        closeButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireCloseEvent();
            }
        });

        html.getElement().getStyle().setMarginRight(0.5, Style.Unit.EM);

        hPanel.add(closeButton);
        closeButton.getElement().getStyle().setMarginTop(0.2, Style.Unit.EM);


        // FIXME really should be vertically aligned to centre ... maybe need some other sort of panel ... but not that one that will do line breaks
        //closeButton.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);

        initWidget(hPanel);
    }


    private void fireCloseEvent() {
        CloseEvent.fire(this, this);
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<CloseableTabWidget> handler) {
        return addHandler(handler, CloseEvent.getType());
    }
}
