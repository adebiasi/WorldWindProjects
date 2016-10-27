package it.graphitech.smeSpire;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwindx.examples.util.HighlightController;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

  class WWPanel extends JPanel
{
    protected WorldWindowGLCanvas wwd;
    protected HighlightController highlightController;

    public WWPanel(WorldWindow shareWith, Model model, Dimension canvasSize)
    {
        super(new BorderLayout(5, 5));

        this.wwd = shareWith != null ? new WorldWindowGLCanvas(shareWith) : new WorldWindowGLCanvas();
        if (canvasSize != null)
            this.wwd.setPreferredSize(canvasSize);
        this.wwd.setModel(model);
        this.add(this.wwd, BorderLayout.CENTER);

        StatusBar statusBar = new StatusBar();
        statusBar.setEventSource(this.wwd);
        this.add(statusBar, BorderLayout.SOUTH);

        this.highlightController = new HighlightController(this.wwd, SelectEvent.ROLLOVER);
    }
    
    public WorldWindow getWwd()
    {
        return this.wwd;
    }
}