package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

public class GuiTextField extends Gui
{
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lisneScrollOffset;
    private int cursorPosition;
    private int slectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private boolean visible = true;
    private static final String __OBFID = "CL_00000670";

    public GuiTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5)
    {
        this.fontRenderer = par1FontRenderer;
        this.xPos = par2;
        this.yPos = par3;
        this.width = par4;
        this.height = par5;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter()
    {
        ++this.cursorCounter;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(String p_146180_1_)
    {
        if (p_146180_1_.length() > this.maxStringLength)
        {
            this.text = p_146180_1_.substring(0, this.maxStringLength);
        }
        else
        {
            this.text = p_146180_1_;
        }

        this.setCursorPositionEnd();
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText()
    {
        return this.text;
    }

    public String getSelectedText()
    {
        int var1 = this.cursorPosition < this.slectionEnd ? this.cursorPosition : this.slectionEnd;
        int var2 = this.cursorPosition < this.slectionEnd ? this.slectionEnd : this.cursorPosition;
        return this.text.substring(var1, var2);
    }

    public void writeText(String p_146191_1_)
    {
        String var2 = "";
        String var3 = ChatAllowedCharacters.filerAllowedCharacters(p_146191_1_);
        int var4 = this.cursorPosition < this.slectionEnd ? this.cursorPosition : this.slectionEnd;
        int var5 = this.cursorPosition < this.slectionEnd ? this.slectionEnd : this.cursorPosition;
        int var6 = this.maxStringLength - this.text.length() - (var4 - this.slectionEnd);
        boolean var7 = false;

        if (this.text.length() > 0)
        {
            var2 = var2 + this.text.substring(0, var4);
        }

        int var8;

        if (var6 < var3.length())
        {
            var2 = var2 + var3.substring(0, var6);
            var8 = var6;
        }
        else
        {
            var2 = var2 + var3;
            var8 = var3.length();
        }

        if (this.text.length() > 0 && var5 < this.text.length())
        {
            var2 = var2 + this.text.substring(var5);
        }

        this.text = var2;
        this.moveCursorBy(var4 - this.slectionEnd + var8);
    }

    public void deleteWords(int p_146177_1_)
    {
        if (this.text.length() != 0)
        {
            if (this.slectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int p_146175_1_)
    {
        if (this.text.length() != 0)
        {
            if (this.slectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                boolean var2 = p_146175_1_ < 0;
                int var3 = var2 ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int var4 = var2 ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String var5 = "";

                if (var3 >= 0)
                {
                    var5 = this.text.substring(0, var3);
                }

                if (var4 < this.text.length())
                {
                    var5 = var5 + this.text.substring(var4);
                }

                this.text = var5;

                if (var2)
                {
                    this.moveCursorBy(p_146175_1_);
                }
            }
        }
    }

    public int getNthWordFromCursor(int p_146187_1_)
    {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_)
    {
        return this.func_146197_a(p_146183_1_, this.getCursorPosition(), true);
    }

    public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_)
    {
        int var4 = p_146197_2_;
        boolean var5 = p_146197_1_ < 0;
        int var6 = Math.abs(p_146197_1_);

        for (int var7 = 0; var7 < var6; ++var7)
        {
            if (var5)
            {
                while (p_146197_3_ && var4 > 0 && this.text.charAt(var4 - 1) == 32)
                {
                    --var4;
                }

                while (var4 > 0 && this.text.charAt(var4 - 1) != 32)
                {
                    --var4;
                }
            }
            else
            {
                int var8 = this.text.length();
                var4 = this.text.indexOf(32, var4);

                if (var4 == -1)
                {
                    var4 = var8;
                }
                else
                {
                    while (p_146197_3_ && var4 < var8 && this.text.charAt(var4) == 32)
                    {
                        ++var4;
                    }
                }
            }
        }

        return var4;
    }

    public void moveCursorBy(int p_146182_1_)
    {
        this.setCursorPosition(this.slectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int p_146190_1_)
    {
        this.cursorPosition = p_146190_1_;
        int var2 = this.text.length();

        if (this.cursorPosition < 0)
        {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > var2)
        {
            this.cursorPosition = var2;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_)
    {
        if (!this.isFocused)
        {
            return false;
        }
        else
        {
            switch (p_146201_1_)
            {
                case 1:
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                    return true;

                case 3:
                    GuiScreen.setClipboardString(this.getSelectedText());
                    return true;

                case 22:
                    if (this.isEnabled)
                    {
                        this.writeText(GuiScreen.getClipboardString());
                    }

                    return true;

                case 24:
                    GuiScreen.setClipboardString(this.getSelectedText());

                    if (this.isEnabled)
                    {
                        this.writeText("");
                    }

                    return true;

                default:
                    switch (p_146201_2_)
                    {
                        case 14:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                if (this.isEnabled)
                                {
                                    this.deleteWords(-1);
                                }
                            }
                            else if (this.isEnabled)
                            {
                                this.deleteFromCursor(-1);
                            }

                            return true;

                        case 199:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(0);
                            }
                            else
                            {
                                this.setCursorPositionZero();
                            }

                            return true;

                        case 203:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() - 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            }
                            else
                            {
                                this.moveCursorBy(-1);
                            }

                            return true;

                        case 205:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() + 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            }
                            else
                            {
                                this.moveCursorBy(1);
                            }

                            return true;

                        case 207:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(this.text.length());
                            }
                            else
                            {
                                this.setCursorPositionEnd();
                            }

                            return true;

                        case 211:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                if (this.isEnabled)
                                {
                                    this.deleteWords(1);
                                }
                            }
                            else if (this.isEnabled)
                            {
                                this.deleteFromCursor(1);
                            }

                            return true;

                        default:
                            if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_))
                            {
                                if (this.isEnabled)
                                {
                                    this.writeText(Character.toString(p_146201_1_));
                                }

                                return true;
                            }
                            else
                            {
                                return false;
                            }
                    }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_)
    {
        boolean var4 = p_146192_1_ >= this.xPos && p_146192_1_ < this.xPos + this.width && p_146192_2_ >= this.yPos && p_146192_2_ < this.yPos + this.height;

        if (this.canLoseFocus)
        {
            this.setFocused(var4);
        }

        if (this.isFocused && p_146192_3_ == 0)
        {
            int var5 = p_146192_1_ - this.xPos;

            if (this.enableBackgroundDrawing)
            {
                var5 -= 4;
            }

            String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.lisneScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.lisneScrollOffset);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox()
    {
        if (this.getVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
                drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
            }

            int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
            int var2 = this.cursorPosition - this.lisneScrollOffset;
            int var3 = this.slectionEnd - this.lisneScrollOffset;
            String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.lisneScrollOffset), this.getWidth());
            boolean var5 = var2 >= 0 && var2 <= var4.length();
            boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
            int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
            int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
            int var9 = var7;

            if (var3 > var4.length())
            {
                var3 = var4.length();
            }

            if (var4.length() > 0)
            {
                String var10 = var5 ? var4.substring(0, var2) : var4;
                var9 = this.fontRenderer.drawStringWithShadow(var10, var7, var8, var1);
            }

            boolean var13 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int var11 = var9;

            if (!var5)
            {
                var11 = var2 > 0 ? var7 + this.width : var7;
            }
            else if (var13)
            {
                var11 = var9 - 1;
                --var9;
            }

            if (var4.length() > 0 && var5 && var2 < var4.length())
            {
                this.fontRenderer.drawStringWithShadow(var4.substring(var2), var9, var8, var1);
            }

            if (var6)
            {
                if (var13)
                {
                    Gui.drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                }
                else
                {
                    this.fontRenderer.drawStringWithShadow("_", var11, var8, var1);
                }
            }

            if (var3 != var2)
            {
                int var12 = var7 + this.fontRenderer.getStringWidth(var4.substring(0, var3));
                this.drawCursorVertical(var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_)
    {
        int var5;

        if (p_146188_1_ < p_146188_3_)
        {
            var5 = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = var5;
        }

        if (p_146188_2_ < p_146188_4_)
        {
            var5 = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = var5;
        }

        if (p_146188_3_ > this.xPos + this.width)
        {
            p_146188_3_ = this.xPos + this.width;
        }

        if (p_146188_1_ > this.xPos + this.width)
        {
            p_146188_1_ = this.xPos + this.width;
        }

        Tessellator var6 = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        var6.startDrawingQuads();
        var6.addVertex((double)p_146188_1_, (double)p_146188_4_, 0.0D);
        var6.addVertex((double)p_146188_3_, (double)p_146188_4_, 0.0D);
        var6.addVertex((double)p_146188_3_, (double)p_146188_2_, 0.0D);
        var6.addVertex((double)p_146188_1_, (double)p_146188_2_, 0.0D);
        var6.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void setMaxStringLength(int p_146203_1_)
    {
        this.maxStringLength = p_146203_1_;

        if (this.text.length() > p_146203_1_)
        {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    public int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean p_146185_1_)
    {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    public void setTextColor(int p_146193_1_)
    {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_)
    {
        this.disabledColor = p_146204_1_;
    }

    /**
     * Sets focus to this gui element
     */
    public void setFocused(boolean p_146195_1_)
    {
        if (p_146195_1_ && !this.isFocused)
        {
            this.cursorCounter = 0;
        }

        this.isFocused = p_146195_1_;
    }

    /**
     * Getter for the focused field
     */
    public boolean isFocused()
    {
        return this.isFocused;
    }

    public void setEnabled(boolean p_146184_1_)
    {
        this.isEnabled = p_146184_1_;
    }

    public int getSelectionEnd()
    {
        return this.slectionEnd;
    }

    public int getWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int p_146199_1_)
    {
        int var2 = this.text.length();

        if (p_146199_1_ > var2)
        {
            p_146199_1_ = var2;
        }

        if (p_146199_1_ < 0)
        {
            p_146199_1_ = 0;
        }

        this.slectionEnd = p_146199_1_;

        if (this.fontRenderer != null)
        {
            if (this.lisneScrollOffset > var2)
            {
                this.lisneScrollOffset = var2;
            }

            int var3 = this.getWidth();
            String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.lisneScrollOffset), var3);
            int var5 = var4.length() + this.lisneScrollOffset;

            if (p_146199_1_ == this.lisneScrollOffset)
            {
                this.lisneScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, var3, true).length();
            }

            if (p_146199_1_ > var5)
            {
                this.lisneScrollOffset += p_146199_1_ - var5;
            }
            else if (p_146199_1_ <= this.lisneScrollOffset)
            {
                this.lisneScrollOffset -= this.lisneScrollOffset - p_146199_1_;
            }

            if (this.lisneScrollOffset < 0)
            {
                this.lisneScrollOffset = 0;
            }

            if (this.lisneScrollOffset > var2)
            {
                this.lisneScrollOffset = var2;
            }
        }
    }

    public void setCanLoseFocus(boolean p_146205_1_)
    {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean getVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_)
    {
        this.visible = p_146189_1_;
    }
}
