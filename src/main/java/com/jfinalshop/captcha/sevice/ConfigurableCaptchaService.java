/*
 * Copyright (c) 2009 Piotr Piastucki
 * 
 * This file is part of Patchca CAPTCHA library.
 * 
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jfinalshop.captcha.sevice;

import com.jfinalshop.captcha.background.SingleColorBackgroundFactory;
import com.jfinalshop.captcha.color.SingleColorFactory;
import com.jfinalshop.captcha.filter.predefined.CurvesRippleFilterFactory;
import com.jfinalshop.captcha.font.RandomFontFactory;
import com.jfinalshop.captcha.text.renderer.BestFitTextRenderer;
import com.jfinalshop.captcha.word.AdaptiveRandomWordFactory;





public class ConfigurableCaptchaService extends AbstractCaptchaService {

  public ConfigurableCaptchaService() {
    backgroundFactory = new SingleColorBackgroundFactory();
    wordFactory = new AdaptiveRandomWordFactory();
    fontFactory = new RandomFontFactory();
    textRenderer = new BestFitTextRenderer();
    colorFactory = new SingleColorFactory();
    filterFactory = new CurvesRippleFilterFactory(colorFactory);
    textRenderer.setLeftMargin(10);
    textRenderer.setRightMargin(10);
    width = 160;
    height = 70;
  }

}
