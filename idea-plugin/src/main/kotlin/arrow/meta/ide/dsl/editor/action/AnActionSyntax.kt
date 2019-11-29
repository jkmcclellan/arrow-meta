package arrow.meta.ide.dsl.editor.action

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.action.AnActionExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.TimerListener
import com.intellij.openapi.application.ModalityState
import javax.swing.Icon

/**
 * [AnActionExtensionProvider] is in charge of the lifecycle of [AnAction].
 * [AnAction] is an overly broad concept to access and execute nearly anything in the editor.
 * Put differently, [AnAction] contains a computation which can be evaluated on-demand by the user.
 * When a user triggers [AnAction] the [AnAction.actionPerformed] function is executed.
 * [AnAction] can form starkly different purposes, from changing CompilerConfigurations in the editor to opening UI element's with media content.
 * Here are a few Links to the Action System from IntelliJ's DevGuid:
 * - [Plugin Actions](http://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_actions.html?search=act)
 * - [Executing and updating actions](http://www.jetbrains.org/intellij/sdk/docs/basics/action_system.html?search=act#executing-and-updating-actions)
 */
interface AnActionSyntax : AnActionUtilitySyntax {

  /**
   * Registers the [action] with [actionId] as it's identifier.
   * ```kotlin:ank:playground
   * import com.intellij.openapi.wm.ToolWindowManager
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.invoke
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   *
   * val IdeMetaPlugin.exampleAction: Plugin
   * get() = "Example Action" {
   *   meta(
   *   //sampleStart
   *     addAnAction(
   *      actionId = "Unique",
   *      action = anAction(
   *       icon = ArrowIcons.ICON1,
   *       actionPerformed = { e ->
   *        e.project?.let { project -> ToolWindowManager.getInstance(project)?.getToolWindow("MetaToolWindow")?.activate(null) }
   *        }
   *       )
   *      )
   *   //sampleEnd
   *    )
   *  }
   * ```
   *
   * This Action `Unique` has an Icon and opens a ToolWindow with a registered Id `MetaToolWindow`, assuming this ToolWindow is registered.
   * User's are now able to search this Action through his [actionId]
   * @param actionId needs to be unique
   * @param action can be composed with various [anAction] implementations
   */
  fun IdeMetaPlugin.addAnAction(
    actionId: String,
    action: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.RegisterAction(actionId, action)

  /**
   * replaces [AnAction] with [actionId] to a [newAction]
   */
  fun IdeMetaPlugin.replaceAnAction(
    actionId: String,
    newAction: AnAction
  ): ExtensionPhase =
    AnActionExtensionProvider.ReplaceAction(actionId, newAction)

  /**
   * unregisters [AnAction]
   */
  fun IdeMetaPlugin.unregisterAnAction(
    actionId: String
  ): ExtensionPhase =
    AnActionExtensionProvider.UnregisterAction(actionId)

  /**
   * registers a [TimerListener]
   */
  fun IdeMetaPlugin.addTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  /**
   * registers a transparent [TimerListener]
   */
  fun IdeMetaPlugin.addTransparentTimerListener(
    delay: Int,
    modalityState: ModalityState,
    run: () -> Unit
  ): ExtensionPhase =
    AnActionExtensionProvider.AddTransparentTimerListener(delay, this@AnActionSyntax.timerListener(modalityState, run))

  /**
   * removes a transparent [TimerListener]
   */
  fun IdeMetaPlugin.removeTransparentTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTransparentTimerListener(listener)

  /**
   * removes a [TimerListener]
   */
  fun IdeMetaPlugin.removeTimerListener(
    listener: TimerListener
  ): ExtensionPhase =
    AnActionExtensionProvider.RemoveTimerListener(listener)

  /**
   * @see org.jetbrains.kotlin.idea.actions.DecompileKotlinToJavaAction
   */
  fun AnActionSyntax.anAction(
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction() { // TODO: Add more costume attributes: ShortCuts etc.
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean): Unit =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(icon) {
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean): Unit =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    title: String,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title) {
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.anAction(
    title: String,
    description: String,
    icon: Icon,
    actionPerformed: (e: AnActionEvent) -> Unit,
    beforeActionPerformedUpdate: (e: AnActionEvent) -> Unit = Noop.effect1,
    update: (e: AnActionEvent) -> Unit = Noop.effect1,
    displayTextInToolbar: Boolean = false,
    setInjectedContext: (worksInInjected: Boolean) -> Boolean = { it },
    useSmallerFontForTextInToolbar: Boolean = false,
    startInTransaction: Boolean = false,
    getTemplateText: String? = null
  ): AnAction =
    object : AnAction(title, description, icon) {
      override fun actionPerformed(e: AnActionEvent): Unit = actionPerformed(e)
      override fun displayTextInToolbar(): Boolean = displayTextInToolbar

      override fun setInjectedContext(worksInInjected: Boolean) =
        super.setInjectedContext(setInjectedContext(worksInInjected))

      override fun update(e: AnActionEvent): Unit = update(e)

      override fun useSmallerFontForTextInToolbar(): Boolean =
        useSmallerFontForTextInToolbar

      override fun startInTransaction(): Boolean =
        startInTransaction

      override fun getTemplateText(): String? {
        return getTemplateText ?: super.getTemplateText()
      }

      override fun beforeActionPerformedUpdate(e: AnActionEvent): Unit =
        beforeActionPerformedUpdate(e)
    }

  fun AnActionSyntax.timerListener(
    modalityState: ModalityState,
    run: () -> Unit
  ): TimerListener =
    object : TimerListener {
      override fun run(): Unit = run()
      override fun getModalityState(): ModalityState = modalityState
    }
}
