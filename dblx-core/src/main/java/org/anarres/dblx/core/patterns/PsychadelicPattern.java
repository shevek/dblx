package org.anarres.dblx.core.patterns;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.pattern.LXPattern;
import org.anarres.dblx.core.palettes.ColorOffset;
import org.anarres.dblx.core.palettes.GeneratorPalette;

/** ************************************************************** PSYCHEDELIC
 * Colors entire brain in modulatable psychadelic color palettes
 * Demo pattern for GeneratorPalette.
 *
 * @author scouras
 ************************************************************************** */
public class PsychadelicPattern extends LXPattern {

    double ms = 0.0;
    double offset = 0.0;
    private final BasicParameter colorScheme = new BasicParameter("SCM", 0, 3);
    private final BasicParameter cycleSpeed = new BasicParameter("SPD", 50, 0, 200);
    private final BasicParameter colorSpread = new BasicParameter("LEN", 100, 0, 1000);
    private final BasicParameter colorHue = new BasicParameter("HUE", 0., 0., 359.);
    private final BasicParameter colorSat = new BasicParameter("SAT", 80., 0., 100.);
    private final BasicParameter colorBrt = new BasicParameter("BRT", 80., 0., 100.);
    private GeneratorPalette gp
            = new GeneratorPalette(
                    new ColorOffset(0xDD0000).setHue(colorHue)
                    .setSaturation(colorSat)
                    .setBrightness(colorBrt),
                    //GeneratorPalette.ColorScheme.Complementary,
                    GeneratorPalette.ColorScheme.Monochromatic,
                    //GeneratorPalette.ColorScheme.Triad,
                    //GeneratorPalette.ColorScheme.Analogous,
                    100
            );
    private int scheme = 0;
    //private EvolutionUC16 EV = EvolutionUC16.getEvolution(lx);

    public PsychadelicPattern(LX lx) {
        super(lx);
        addParameter(colorScheme);
        addParameter(cycleSpeed);
        addParameter(colorSpread);
        addParameter(colorHue);
        addParameter(colorSat);
        addParameter(colorBrt);
        /*println("Did we find an EV? ");
         println(EV);
         EV.bindKnob(colorHue, 0);
         EV.bindKnob(colorSat, 8);
         EV.bindKnob(colorBrt, 7);
         */
    }

    public void run(double deltaMs) {
        int newScheme = (int) Math.floor(colorScheme.getValue());
        if (newScheme != scheme) {
            switch (newScheme) {
                case 0:
                    gp.setScheme(GeneratorPalette.ColorScheme.Analogous);
                    break;
                case 1:
                    gp.setScheme(GeneratorPalette.ColorScheme.Monochromatic);
                    break;
                case 2:
                    gp.setScheme(GeneratorPalette.ColorScheme.Triad);
                    break;
                case 3:
                    gp.setScheme(GeneratorPalette.ColorScheme.Complementary);
                    break;
            }
            scheme = newScheme;
        }

        ms += deltaMs;
        offset += deltaMs * cycleSpeed.getValue() / 1000.;
        int steps = (int) colorSpread.getValue();
        if (steps != gp.steps) {
            gp.setSteps(steps);
        }
        gp.reset((int) offset);
        for (LXPoint p : model.points) {
            colors[p.index] = gp.getColor();
        }
    }
}
