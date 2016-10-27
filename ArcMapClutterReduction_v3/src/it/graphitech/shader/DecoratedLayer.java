package it.graphitech.shader;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.Message;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

public class DecoratedLayer implements Layer {
    private final Layer _layer;
    private final ShadingDecorator _decorator;

    public DecoratedLayer(Layer layer, ShadingDecorator decorator) {
        _layer = layer;
        _decorator = decorator;
    }

    
    public Layer get_layer() {
		return _layer;
	}


	@Override
    public void preRender(DrawContext dc) {
    	//System.out.println("DecoratedLayer preRender");
       _decorator.preRender(dc, _layer);
    }

    @Override
    public void render(DrawContext dc) {
    	//System.out.println("DecoratedLayer render");
       _decorator.render(dc, _layer);
    }

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		_layer.addPropertyChangeListener(arg0);
	}

	public void addPropertyChangeListener(String arg0,
			PropertyChangeListener arg1) {
		_layer.addPropertyChangeListener(arg0, arg1);
	}

	public AVList clearList() {
		return _layer.clearList();
	}

	public AVList copy() {
		return _layer.copy();
	}

	public void dispose() {
		_layer.dispose();
	}

	public void firePropertyChange(PropertyChangeEvent arg0) {
		_layer.firePropertyChange(arg0);
	}

	public void firePropertyChange(String arg0, Object arg1, Object arg2) {
		_layer.firePropertyChange(arg0, arg1, arg2);
	}

	public Set<Entry<String, Object>> getEntries() {
		return _layer.getEntries();
	}

	public long getExpiryTime() {
		return _layer.getExpiryTime();
	}

	public double getMaxActiveAltitude() {
		return _layer.getMaxActiveAltitude();
	}

	public Double getMaxEffectiveAltitude(Double arg0) {
		return _layer.getMaxEffectiveAltitude(arg0);
	}

	public double getMinActiveAltitude() {
		return _layer.getMinActiveAltitude();
	}

	public Double getMinEffectiveAltitude(Double arg0) {
		return _layer.getMinEffectiveAltitude(arg0);
	}

	public String getName() {
		return _layer.getName();
	}

	public double getOpacity() {
		return _layer.getOpacity();
	}

	public String getRestorableState() {
		return _layer.getRestorableState();
	}

	public double getScale() {
		return _layer.getScale();
	}

	public String getStringValue(String arg0) {
		return _layer.getStringValue(arg0);
	}

	public Object getValue(String arg0) {
		return _layer.getValue(arg0);
	}

	public Collection<Object> getValues() {
		return _layer.getValues();
	}

	public boolean hasKey(String arg0) {
		return _layer.hasKey(arg0);
	}

	public boolean isAtMaxResolution() {
		return _layer.isAtMaxResolution();
	}

	public boolean isEnabled() {
		return _layer.isEnabled();
	}

	public boolean isLayerActive(DrawContext arg0) {
		return _layer.isLayerActive(arg0);
	}

	public boolean isLayerInView(DrawContext arg0) {
		return _layer.isLayerInView(arg0);
	}

	public boolean isMultiResolution() {
		return _layer.isMultiResolution();
	}

	public boolean isNetworkRetrievalEnabled() {
		return _layer.isNetworkRetrievalEnabled();
	}

	public boolean isPickEnabled() {
		return _layer.isPickEnabled();
	}

	public void onMessage(Message arg0) {
		_layer.onMessage(arg0);
	}

	public void pick(DrawContext arg0, Point arg1) {
		_layer.pick(arg0, arg1);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		_layer.propertyChange(arg0);
	}

	public Object removeKey(String arg0) {
		return _layer.removeKey(arg0);
	}

	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		_layer.removePropertyChangeListener(arg0);
	}

	public void removePropertyChangeListener(String arg0,
			PropertyChangeListener arg1) {
		_layer.removePropertyChangeListener(arg0, arg1);
	}

	public void restoreState(String arg0) {
		_layer.restoreState(arg0);
	}

	public void setEnabled(boolean arg0) {
		_layer.setEnabled(arg0);
	}

	public void setExpiryTime(long arg0) {
		_layer.setExpiryTime(arg0);
	}

	public void setMaxActiveAltitude(double arg0) {
		_layer.setMaxActiveAltitude(arg0);
	}

	public void setMinActiveAltitude(double arg0) {
		_layer.setMinActiveAltitude(arg0);
	}

	public void setName(String arg0) {
		_layer.setName(arg0);
	}

	public void setNetworkRetrievalEnabled(boolean arg0) {
		_layer.setNetworkRetrievalEnabled(arg0);
	}

	public void setOpacity(double arg0) {
		_layer.setOpacity(arg0);
	}

	public void setPickEnabled(boolean arg0) {
		_layer.setPickEnabled(arg0);
	}

	public Object setValue(String arg0, Object arg1) {
		return _layer.setValue(arg0, arg1);
	}

	public AVList setValues(AVList arg0) {
		return _layer.setValues(arg0);
	}

	
	
    
    // all other methods delegate to _layer
}


