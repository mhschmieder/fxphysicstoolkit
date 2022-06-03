/**
 * MIT License
 *
 * Copyright (c) 2020, 2022 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxPhysicsToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxPhysicsToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxphysicstoolkit
 */
package com.mhschmieder.fxphysicstoolkit.layout;

import java.text.NumberFormat;

import com.mhschmieder.commonstoolkit.lang.StringConstants;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxgraphicstoolkit.paint.ColorUtilities;
import com.mhschmieder.fxguitoolkit.GuiUtilities;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.fxphysicstoolkit.model.NaturalEnvironment;
import com.mhschmieder.physicstoolkit.PressureUnit;
import com.mhschmieder.physicstoolkit.TemperatureUnit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class NaturalEnvironmentInformationPane extends VBox {

    // Declare strings for the static part of the settings formatting.
    public static final String                          AIR_ATTENUATION_LABEL_LABEL     =
                                                                                    "Air Attenuation";                      //$NON-NLS-1$

    public static final String                          TEMPERATURE_LABEL_LABEL         =
                                                                                "Temperature";                              //$NON-NLS-1$

    public static final String                          PRESSURE_LABEL_LABEL            =
                                                                             "Pressure";                                    //$NON-NLS-1$

    public static final String                          RELATIVE_HUMIDITY_LABEL_LABEL   =
                                                                                      "Relative Humidity";                  //$NON-NLS-1$

    // Declare default formatted data for each label.
    public static final String                          AIR_ATTENUATION_LABEL_DEFAULT   =
                                                                                      AIR_ATTENUATION_LABEL_LABEL
                                                                                              + " Off";                     //$NON-NLS-1$
    @SuppressWarnings("nls") public static final String TEMPERATURE_LABEL_DEFAULT       =
                                                                                  TEMPERATURE_LABEL_LABEL
                                                                                          + " = 20"
                                                                                          + StringConstants.DEGREES_CELSIUS;
    @SuppressWarnings("nls") public static final String PRESSURE_LABEL_DEFAULT          =
                                                                               PRESSURE_LABEL_LABEL
                                                                                       + " = 101325 "
                                                                                       + PressureUnit.PASCALS
                                                                                               .toPresentationString();
    @SuppressWarnings("nls") public static final String RELATIVE_HUMIDITY_LABEL_DEFAULT =
                                                                                        RELATIVE_HUMIDITY_LABEL_LABEL
                                                                                                + " = 50%";

    public static String getAirAttenuationLabel( final NaturalEnvironment naturalEnvironment ) {
        final String airAttenuationLabel = AIR_ATTENUATION_LABEL_LABEL
                + ( naturalEnvironment.isAirAttenuationApplied() ? " On" : " Off" ); //$NON-NLS-1$ //$NON-NLS-2$
        return airAttenuationLabel;
    }

    @SuppressWarnings("nls")
    public static String getPressureLabel( final NaturalEnvironment naturalEnvironment,
                                           final PressureUnit pressureUnit,
                                           final NumberFormat numberFormat ) {
        numberFormat.setMinimumFractionDigits( 2 );
        numberFormat.setMaximumFractionDigits( 2 );
        final String pressureLabel = PRESSURE_LABEL_LABEL + " = "
                + numberFormat.format( naturalEnvironment.getPressure( pressureUnit ) ) + " "
                + pressureUnit.toPresentationString();
        return pressureLabel;
    }

    @SuppressWarnings("nls")
    public static String getRelativeHumidityLabel( final NaturalEnvironment naturalEnvironment,
                                                   final NumberFormat percentFormat ) {
        percentFormat.setMinimumFractionDigits( 1 );
        percentFormat.setMaximumFractionDigits( 1 );
        final String relativeHumidityLabel = RELATIVE_HUMIDITY_LABEL_LABEL + " = "
                + percentFormat.format( naturalEnvironment.getHumidityRelative() * 0.01d );
        return relativeHumidityLabel;
    }

    @SuppressWarnings("nls")
    public static String getTemperatureLabel( final NaturalEnvironment naturalEnvironment,
                                              final TemperatureUnit temperatureUnit,
                                              final NumberFormat numberFormat ) {
        numberFormat.setMinimumFractionDigits( 1 );
        numberFormat.setMaximumFractionDigits( 1 );
        final String temperatureLabel = TEMPERATURE_LABEL_LABEL + " = "
                + numberFormat.format( naturalEnvironment.getTemperature( temperatureUnit ) )
                + temperatureUnit.toPresentationString();
        return temperatureLabel;
    }

    public Label               _airAttenuationLabel;
    public Label               _temperatureLabel;
    public Label               _pressureLabel;
    public Label               _relativeHumidityLabel;

    // Keep a cached copy of the Natural Environment reference, in case the
    // units are changed between predictions.
    private NaturalEnvironment _naturalEnvironment;

    // Keep track of what units we're using to display, for later conversion.
    private TemperatureUnit    _temperatureUnit;
    private PressureUnit       _pressureUnit;

    // Number format cache used for locale-specific number formatting.
    protected NumberFormat     _numberFormat;

    // Percent format cache used for locale-specific percent formatting.
    protected NumberFormat     _percentFormat;

    public NaturalEnvironmentInformationPane( final ClientProperties clientProperties ) {
        // Always call the superclass constructor first!
        super();

        _temperatureUnit = TemperatureUnit.defaultValue();
        _pressureUnit = PressureUnit.defaultValue();

        try {
            initPane( clientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties clientProperties ) {
        // Cache the number formats so that we don't have to get information
        // about locale, language, etc. from the OS each time we format a
        // number.
        _numberFormat = NumberFormat.getNumberInstance( clientProperties.locale );
        _percentFormat = NumberFormat.getPercentInstance( clientProperties.locale );

        _airAttenuationLabel = GuiUtilities.getStatusLabel( AIR_ATTENUATION_LABEL_DEFAULT );
        _temperatureLabel = GuiUtilities.getStatusLabel( TEMPERATURE_LABEL_DEFAULT );
        _pressureLabel = GuiUtilities.getStatusLabel( PRESSURE_LABEL_DEFAULT );
        _relativeHumidityLabel = GuiUtilities.getStatusLabel( RELATIVE_HUMIDITY_LABEL_DEFAULT );

        getChildren().addAll( _airAttenuationLabel,
                              _temperatureLabel,
                              _pressureLabel,
                              _relativeHumidityLabel );
        setAlignment( Pos.CENTER_LEFT );

        setPadding( new Insets( 6.0d ) );
    }

    public void reset() {
        _airAttenuationLabel.setText( AIR_ATTENUATION_LABEL_DEFAULT );
        _temperatureLabel.setText( TEMPERATURE_LABEL_DEFAULT );
        _pressureLabel.setText( PRESSURE_LABEL_DEFAULT );
        _relativeHumidityLabel.setText( RELATIVE_HUMIDITY_LABEL_DEFAULT );
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );
        _airAttenuationLabel.setTextFill( foregroundColor );
        _temperatureLabel.setTextFill( foregroundColor );
        _pressureLabel.setTextFill( foregroundColor );
        _relativeHumidityLabel.setTextFill( foregroundColor );
    }

    // Set and propagate the Natural Environment reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setNaturalEnvironment( final NaturalEnvironment naturalEnvironment ) {
        // Cache the current Natural Environment in case the Measurement
        // Units change before the next prediction is run.
        _naturalEnvironment = naturalEnvironment;

        // Load the invalidation listener for the "Natural Environment Changed"
        // binding.
        _naturalEnvironment.naturalEnvironmentChanged.addListener( invalidationListener -> {
            // Clear the invalidation and process the change.
            if ( _naturalEnvironment.naturalEnvironmentChanged.getValue() ) {
                updateLabels();
            }
        } );
    }

    public void syncViewToModel() {
        updateLabels();
    }

    public void updateLabels() {
        final String airAttenuationLabel = getAirAttenuationLabel( _naturalEnvironment );
        _airAttenuationLabel.setText( airAttenuationLabel );

        final String temperatureLabel = getTemperatureLabel( _naturalEnvironment,
                                                             _temperatureUnit,
                                                             _numberFormat );
        _temperatureLabel.setText( temperatureLabel );

        final String pressureLabel = getPressureLabel( _naturalEnvironment,
                                                       _pressureUnit,
                                                       _numberFormat );
        _pressureLabel.setText( pressureLabel );

        final String relativeHumidityLabel = getRelativeHumidityLabel( _naturalEnvironment,
                                                                       _percentFormat );
        _relativeHumidityLabel.setText( relativeHumidityLabel );
    }

    public void updatePressureUnit( final PressureUnit pressureUnit ) {
        _pressureUnit = pressureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }

    public void updateTemperatureUnit( final TemperatureUnit temperatureUnit ) {
        _temperatureUnit = temperatureUnit;

        // Update the labels in the table to reflect the new units.
        updateLabels();
    }

}
