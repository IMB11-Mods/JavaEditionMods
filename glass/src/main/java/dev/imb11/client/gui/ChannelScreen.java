package dev.imb11.client.gui;

import dev.imb11.client.ClientProjectionSourceRegistry;
import dev.imb11.client.renderer.projection.ProjectionRenderManager;
import dev.imb11.platform.ClientNetworking;
import dev.imb11.sync.Channel;
import dev.imb11.sync.ChannelManagerPersistence;
import dev.imb11.sync.packets.C2SCreateChannelPacket;
import dev.imb11.sync.packets.C2SDeleteChannelPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class ChannelScreen<M extends ChannelMenu> extends AbstractContainerScreen<M> {
    private final boolean manage;
    private List<Channel> channels = List.of();
    private long revision = -1;
    private String selectedName;
    private String currentName;
    private String pendingCreation;
    private int pendingTicks;
    private ProjectionRenderManager.ProjectionFeed preview;
    private boolean initialized;
    private boolean confirmingDelete;
    private EditBox search;
    private ChannelList list;
    private Button choose;
    private Button delete;
    private Button cancel;
    private Button unlink;
    private int listWidth;
    private int detailX;
    private int detailWidth;
    private int matches;
    private int panelWidth;
    private int panelHeight;

    protected ChannelScreen(M menu, Inventory inventory, Component title, boolean manage) {
        super(menu, inventory, title);
        this.manage = manage;
    }

    protected abstract boolean isCurrent(Channel channel);

    protected abstract boolean canChoose(Channel channel);

    protected abstract void choose(Channel channel);

    protected abstract Component chooseLabel();

    protected abstract Component status(Channel channel);

    protected abstract String unavailableLabel();

    protected void unlink() {
    }

    @Override
    protected void init() {
        String query = search == null ? "" : search.getValue();
        double scroll = list == null ? 0 : list.scrollAmount();
        panelWidth = Math.min(620, width - 16);
        panelHeight = Math.min(340, height - 16);
        super.init();
        leftPos = (width - panelWidth) / 2;
        topPos = (height - panelHeight) / 2;
        listWidth = Math.max(112, panelWidth * 35 / 100);
        detailX = leftPos + listWidth + 12;
        detailWidth = panelWidth - listWidth - 24;
        refreshChannels();
        if (!initialized) {
            Channel current = current();
            selectedName = current == null ? null : current.name();
        }
        search = addRenderableWidget(new EditBox(font, leftPos + 10, topPos + 36, listWidth - 18, 20,
                Component.literal(manage ? "Search or create channel" : "Search channels")));
        search.setMaxLength(ChannelManagerPersistence.MAX_CHANNEL_NAME_LENGTH);
        search.setHint(Component.literal(manage ? "Search or create…" : "Search…"));
        search.setValue(query);
        list = addRenderableWidget(new ChannelList(leftPos + 6, topPos + 76, listWidth - 8, panelHeight - 84));
        search.setResponder(value -> {
            confirmingDelete = false;
            rebuildList();
            list.setScrollAmount(0);
            updateActions();
        });
        int actionY = topPos + panelHeight - 30;
        int deleteWidth = manage ? Math.min(64, detailWidth / 3) : 0;
        int chooseWidth = manage ? detailWidth - deleteWidth - 6 : detailWidth;
        choose = addRenderableWidget(Button.builder(chooseLabel(), button -> {
            Channel selected = selected();
            if (selected != null && canChoose(selected)) {
                choose(selected);
            }
        }).bounds(detailX, actionY, chooseWidth, 20).build());
        if (manage) {
            unlink = addRenderableWidget(Button.builder(Component.literal("Unlink"), button -> unlink())
                    .bounds(leftPos + panelWidth - 66, topPos + 6, 56, 20).build());
            cancel = addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> {
                confirmingDelete = false;
                updateActions();
            }).bounds(detailX, actionY, chooseWidth, 20).build());
            delete = addRenderableWidget(Button.builder(Component.literal("Delete"), button -> {
                Channel selected = selected();
                if (selected == null || ChannelManagerPersistence.DEFAULT_CHANNEL.equals(selected.name())) {
                    return;
                }
                if (confirmingDelete) {
                    ClientNetworking.send(new C2SDeleteChannelPacket(menu.pos, selected.name()));
                    confirmingDelete = false;
                } else {
                    confirmingDelete = true;
                }
                updateActions();
            }).bounds(detailX + detailWidth - deleteWidth, actionY, deleteWidth, 20).build());
        }
        rebuildList();
        list.setScrollAmount(scroll);
        if (!initialized && list.getSelected() != null) {
            list.revealSelection();
        }
        initialized = true;
        updateActions();
    }

    private void refreshChannels() {
        revision = ClientProjectionSourceRegistry.registryRevision();
        channels = ClientProjectionSourceRegistry.channels().values().stream()
                .sorted(Comparator.comparing(Channel::name, String.CASE_INSENSITIVE_ORDER).thenComparing(Channel::name)).toList();
        Channel current = current();
        currentName = current == null ? null : current.name();
        if (pendingCreation != null && channels.stream().anyMatch(channel -> channel.name().equals(pendingCreation))) {
            selectedName = pendingCreation;
            pendingCreation = null;
        }
        if (selectedName != null && selected() == null) {
            selectedName = null;
            confirmingDelete = false;
        }
    }

    private Channel current() {
        return channels.stream().filter(this::isCurrent).findFirst().orElse(null);
    }

    private Channel selected() {
        return channels.stream().filter(channel -> channel.name().equals(selectedName)).findFirst().orElse(null);
    }

    private String creationName() {
        if (!manage) {
            return null;
        }
        String name = ChannelManagerPersistence.canonicalChannelName(search.getValue());
        return name != null && channels.stream().noneMatch(channel -> channel.name().equals(name)) ? name : null;
    }

    private void createChannel() {
        String name = creationName();
        if (name != null && pendingCreation == null && channels.size() < ChannelManagerPersistence.MAX_CHANNELS) {
            pendingCreation = name;
            pendingTicks = 100;
            ClientNetworking.send(new C2SCreateChannelPacket(menu.pos, name));
        }
    }

    private void rebuildList() {
        double scroll = list.scrollAmount();
        list.clearRows();
        String query = search.getValue().strip().toLowerCase(Locale.ROOT);
        matches = 0;
        for (Channel channel : channels) {
            if (channel.name().toLowerCase(Locale.ROOT).contains(query)) {
                ChannelRow row = new ChannelRow(channel, null);
                list.addRow(row);
                matches++;
                if (channel.name().equals(selectedName)) {
                    list.setSelected(row);
                }
            }
        }
        String name = creationName();
        if (name != null) {
            list.addRow(new ChannelRow(null, name));
        }
        list.setScrollAmount(scroll);
    }

    private void updateActions() {
        Channel selected = selected();
        choose.visible = selected != null && !isCurrent(selected) && !confirmingDelete;
        choose.active = selected != null && canChoose(selected);
        if (manage) {
            unlink.visible = current() != null;
            delete.visible = selected != null;
            delete.active = selected != null && !ChannelManagerPersistence.DEFAULT_CHANNEL.equals(selected.name());
            delete.setTooltip(delete.active ? null : Tooltip.create(Component.literal("The Default channel cannot be deleted.")));
            cancel.visible = selected != null && confirmingDelete;
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (pendingCreation != null && --pendingTicks <= 0) {
            pendingCreation = null;
        }
        Channel current = current();
        boolean currentChanged = !Objects.equals(currentName, current == null ? null : current.name());
        if (ClientProjectionSourceRegistry.registryRevision() > revision) {
            boolean created = pendingCreation != null;
            refreshChannels();
            confirmingDelete = false;
            rebuildList();
            if (created && pendingCreation == null && list.getSelected() != null) {
                list.revealSelection();
            }
            updateActions();
        } else if (currentChanged) {
            currentName = current == null ? null : current.name();
            updateActions();
        }
    }

    public void preparePreview() {
        Channel selected = selected();
        if (selected == null || selected.source() == null) {
            releasePreview();
            return;
        }
        preview = ProjectionRenderManager.requestPreview(selected.source(), menu.pos,
                (float) detailWidth / Math.max(1, previewBottom() - topPos - 38));
    }

    private void releasePreview() {
        if (preview != null && minecraft.level != null) {
            ProjectionRenderManager.releasePreview(minecraft.level, menu.pos);
        }
        preview = null;
    }

    @Override
    public void removed() {
        releasePreview();
        super.removed();
    }

    private Component detailStatus(Channel selected) {
        if (confirmingDelete) {
            return Component.literal("Delete " + selected.name() + "? This cannot be undone."
                    + (selected.source() == null ? "" : " Unlinks terminal at " + selected.source().pos().toShortString()
                    + " (" + selected.source().dimension().identifier() + ")."));
        }
        return status(selected);
    }

    private int previewBottom() {
        Channel selected = selected();
        int lines = selected == null ? 2 : font.split(detailStatus(selected), detailWidth).size();
        return Math.max(topPos + 66, topPos + panelHeight - Math.max(88, 60 + lines * font.lineHeight));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        InventoryPanels.panel(graphics, leftPos, topPos, panelWidth, panelHeight);
        Channel selected = selected();
        int previewTop = topPos + 38;
        int previewBottom = previewBottom();
        InventoryPanels.inset(graphics, detailX, previewTop, detailWidth, previewBottom - previewTop);
        if (selected != null) {
            boolean ready = preview != null && selected.source() != null && selected.source().equals(preview.source())
                    && ProjectionRenderManager.isReady(preview);
            if (ready) {
                graphics.blit(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, preview.textureLocation(), detailX + 1, previewTop + 1,
                        0.0F, 1.0F, detailWidth - 2, previewBottom - previewTop - 2, 1, -1, 1, 1);
            } else {
                String message = selected.source() == null ? "No linked camera"
                        : preview != null && preview.isFailed() ? "Camera unavailable" : "Loading camera…";
                graphics.centeredText(font, fit(message, detailWidth - 12),
                        detailX + detailWidth / 2, (previewTop + previewBottom) / 2 - 4, 0xFFFFFFFF);
            }
            int textY = previewBottom + 8;
            graphics.text(font, fit(selected.name(), detailWidth), detailX, textY, 0xFF404040, false);
            graphics.textWithWordWrap(font, detailStatus(selected), detailX, textY + 14, detailWidth, confirmingDelete ? 0xFFA02020 : 0xFF404040, false);
        } else {
            graphics.centeredText(font, "Select a channel", detailX + detailWidth / 2, (previewTop + previewBottom) / 2 - 4, 0xFFFFFFFF);
            graphics.textWithWordWrap(font, Component.literal(manage ? "Select a channel to preview it, or search to create one."
                    : "Select a channel to preview it."), detailX, previewBottom + 10, detailWidth, 0xFF404040, false);
        }
        super.extractContents(graphics, mouseX, mouseY, partialTick);
    }

    private String fit(String text, int availableWidth) {
        return font.width(text) <= availableWidth ? text : font.plainSubstrByWidth(text, Math.max(0, availableWidth - font.width("…"))) + "…";
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 10, 12, 0xFF404040, false);
        Channel current = current();
        String status = current == null ? "Not linked" : "Linked to " + current.name();
        int right = panelWidth - (unlink != null && unlink.visible ? 72 : 10);
        status = fit(status, Math.max(0, right - font.width(title) - 30));
        graphics.text(font, status, right - font.width(status), 12, 0xFF404040, false);
        graphics.text(font, "Channels", 10, 63, 0xFF404040, false);
        String count = matches == channels.size() ? Integer.toString(channels.size()) : matches + " of " + channels.size();
        graphics.text(font, count, listWidth - 10 - font.width(count), 63, 0xFF404040, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == 256 && confirmingDelete) {
            confirmingDelete = false;
            updateActions();
            return true;
        }
        if (search.isFocused() && keyCode != 256 && keyCode != 258) {
            if (keyCode == 264 && !list.children().isEmpty()) {
                setFocused(list);
                list.setFocused(list.getSelected() == null ? list.children().getFirst() : list.getSelected());
                return true;
            }
            if (keyCode == 257 || keyCode == 335) {
                createChannel();
                return true;
            }
            return search.keyPressed(event) || search.canConsumeInput();
        }
        return super.keyPressed(event);
    }

    private final class ChannelList extends ObjectSelectionList<ChannelRow> {
        private ChannelList(int x, int y, int width, int height) {
            super(ChannelScreen.this.minecraft, width, height, y, 22);
            setX(x);
        }

        private void clearRows() {
            setFocused(null);
            clearEntries();
        }

        private void addRow(ChannelRow row) {
            addEntry(row);
        }

        private void revealSelection() {
            scrollToEntry(getSelected());
        }

        @Override
        protected void extractListBackground(GuiGraphicsExtractor graphics) {
            InventoryPanels.inset(graphics, getX(), getY(), getWidth(), getHeight());
        }

        @Override
        protected void extractListSeparators(GuiGraphicsExtractor graphics) {
            InventoryPanels.insetBorder(graphics, getX(), getY(), getWidth(), getHeight());
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
            if (children().isEmpty()) {
                graphics.textWithWordWrap(font, Component.literal("No matching channels"), getX() + 8, getY() + 8, getWidth() - 16, 0xFFFFFFFF);
            }
        }

        @Override
        public int getRowWidth() {
            return getWidth() - 16;
        }

        @Override
        protected int scrollBarX() {
            return getRight() - 6;
        }

        @Override
        public void setSelected(ChannelRow row) {
            super.setSelected(row);
            if (row != null && row.channel != null) {
                selectedName = row.channel.name();
                confirmingDelete = false;
                if (choose != null) {
                    updateActions();
                }
            }
        }
    }

    private final class ChannelRow extends ObjectSelectionList.Entry<ChannelRow> {
        private final Channel channel;
        private final String createName;

        private ChannelRow(Channel channel, String createName) {
            this.channel = channel;
            this.createName = createName;
        }

        private String rowStatus() {
            return channel == null || (!isCurrent(channel) && canChoose(channel)) ? ""
                    : isCurrent(channel) ? "selected" : unavailableLabel();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int x = getContentX();
            int y = getContentY();
            int width = getContentWidth();
            String status = rowStatus();
            String name = channel == null ? "+ Create \"" + createName + "\"" : channel.name();
            boolean atLimit = channel == null && channels.size() >= ChannelManagerPersistence.MAX_CHANNELS;
            String shown = fit(name, width - font.width(status) - 12);
            graphics.text(font, shown, x + 3, y + 6, atLimit ? 0xFFB0B0B0 : 0xFFFFFFFF, true);
            graphics.text(font, status, x + width - font.width(status) - 3, y + 6, 0xFFFFFFFF, true);
            if (hovered && atLimit) {
                graphics.setTooltipForNextFrame(Component.literal("Channel limit reached (" + ChannelManagerPersistence.MAX_CHANNELS + ")"), mouseX, mouseY);
            } else if (hovered && !shown.equals(name)) {
                graphics.setTooltipForNextFrame(Component.literal(name), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
            int button = event.button();
            if (button != 0) {
                return false;
            }
            if (channel == null) {
                createChannel();
            } else {
                list.setSelected(this);
            }
            return true;
        }

        @Override
        public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int keyCode = event.key();
            if (channel == null && (keyCode == 257 || keyCode == 335 || keyCode == 32)) {
                createChannel();
                return true;
            }
            return super.keyPressed(event);
        }

        @Override
        public Component getNarration() {
            String status = rowStatus();
            return Component.literal(channel == null ? "Create " + createName : channel.name() + (status.isEmpty() ? "" : ", " + status));
        }
    }
}
