package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rirose on 5/18/2017.
 * Style Sheet
 */
public class styleSheet {
    private Color black = Color.black.brighter().brighter().brighter().brighter();
    public void setStyle( Component comp ){
        comp.setForeground( Color.white );
        comp.setBackground( black );
    }
    public void setUI(){
        UIManager.put( "TabbedPane.background", black );
        UIManager.put( "TabbedPane.contentAreaColor", black );
        UIManager.put( "TabbedPane.light", black );
        UIManager.put( "TabbedPane.highlight", black );
        UIManager.put( "TabbedPane.shadow", black );
        UIManager.put( "TabbedPane.darkShadow", black );
        UIManager.put( "TabbedPane.selected", black );
        UIManager.put( "TabbedPane.borderHighlightColor", black );
    }
}
