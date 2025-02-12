package arrow.meta.phases.analysis

import arrow.meta.quotes.Scope
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.element.CatchClause
import arrow.meta.quotes.element.FinallySection
import arrow.meta.quotes.element.ImportDirective
import arrow.meta.quotes.element.ParameterList
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.element.WhenEntry
import arrow.meta.quotes.element.whencondition.WhenCondition
import arrow.meta.quotes.expression.AnnotatedExpression
import arrow.meta.quotes.expression.BinaryExpression
import arrow.meta.quotes.expression.BlockExpression
import arrow.meta.quotes.expression.IfExpression
import arrow.meta.quotes.expression.IsExpression
import arrow.meta.quotes.expression.LambdaExpression
import arrow.meta.quotes.expression.ThrowExpression
import arrow.meta.quotes.expression.TryExpression
import arrow.meta.quotes.expression.WhenExpression
import arrow.meta.quotes.expression.expressionwithlabel.PropertyAccessor
import arrow.meta.quotes.expression.expressionwithlabel.ReturnExpression
import arrow.meta.quotes.expression.loopexpression.ForExpression
import arrow.meta.quotes.expression.loopexpression.WhileExpression
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.modifierlist.ModifierList
import arrow.meta.quotes.modifierlist.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import arrow.meta.quotes.nameddeclaration.stub.Parameter
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.TypeAlias
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtBlockCodeFragment
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment
import org.jetbrains.kotlin.psi.KtFunctionTypeReceiver
import org.jetbrains.kotlin.psi.KtInitializerList
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSimpleNameStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeCodeFragment
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.ImportPath

interface ElementScope {
  
  val valKeyword: PsiElement
  
  val varKeyword: PsiElement
  
  val String.expression: Scope<KtExpression>

  val String.dotQualifiedExpression: Scope<KtDotQualifiedExpression>

  val String.expressionOrNull: Scope<KtExpression>
  
  val thisExpression: Scope<KtThisExpression>
  
  val String.thisExpression: Scope<KtThisExpression>
  
  val String.callArguments: Scope<KtValueArgumentList>
  
  val String.typeArguments: Scope<KtTypeArgumentList>
  
  val String.typeArgument: Scope<KtTypeProjection>
  
  val String.type: TypeReference
  
  val KtTypeElement.type: TypeReference

  val String.typeOrNull: Scope<KtTypeReference>
  
  val KtTypeReference.functionTypeReceiver: Scope<KtFunctionTypeReceiver>
  
  val KtTypeReference.functionTypeParameter: Parameter
  
  fun typeAlias(
    name: String,
    typeParameters: List<String>,
    typeElement: KtTypeElement
  ): TypeAlias
  
  fun typeAlias(
    name: String,
    typeParameters: List<String>,
    body: String
  ): TypeAlias
  
  val star: PsiElement
  
  val comma: PsiElement
  
  val dot: PsiElement
  
  val colon: PsiElement
  
  val eq: PsiElement
  
  val semicolon: PsiElement
  
  val whiteSpaceAndArrow: Pair<PsiElement, PsiElement>
  
  val whiteSpace: PsiElement
  
  val String.whiteSpace: PsiElement
  
  val Int.newLine: PsiElement
  
  val String.`class`: ClassDeclaration
  
  val String.`object`: ObjectDeclaration
  
  val companionObject: ObjectDeclaration
  
  val String.companionObject: ObjectDeclaration

  fun file(
    fileName: String,
    text: String
  ): File

  val <A: KtDeclaration> Scope<A>.synthetic: Scope<A>
  
  fun property(
    modifiers: String?,
    name: String,
    type: String?,
    isVar: Boolean,
    initializer: String?
  ): Property
  
  fun property(
    name: String,
    type: String?,
    isVar: Boolean,
    initializer: String?
  ): Property
  
  fun property(
    name: String,
    type: String?,
    isVar: Boolean
  ): Property
  
  val String.property: Property
  
  fun propertyGetter(expression: KtExpression): PropertyAccessor
  
  fun propertySetter(expression: KtExpression): PropertyAccessor
  
  fun propertyDelegate(expression: KtExpression): Scope<KtPropertyDelegate>
  
  val String.destructuringDeclaration: DestructuringDeclaration
  
  fun <A : KtDeclaration> String.declaration(): Scope<A>
  
  val String.nameIdentifier: PsiElement
  
  val String.nameIdentifierIfPossible: PsiElement?
  
  val String.simpleName: Scope<KtSimpleNameExpression>
  
  val String.operationName: Scope<KtSimpleNameExpression>
  
  val String.identifier: PsiElement
  
  val String.function: NamedFunction

  val String.binaryExpression: BinaryExpression
  
  val String.callableReferenceExpression: Scope<KtCallableReferenceExpression>
  
  val String.secondaryConstructor: Scope<KtSecondaryConstructor>
  
  fun modifierList(modifier: KtModifierKeywordToken): ModifierList
  
  val String.modifierList: ModifierList
  
  val emptyModifierList: ModifierList
  
  fun modifier(modifier: KtModifierKeywordToken): PsiElement
  
  val String.annotationEntry: Scope<KtAnnotationEntry>
  
  val emptyBody: BlockExpression
  
  val anonymousInitializer: Scope<KtAnonymousInitializer>
  
  val emptyClassBody: Scope<KtClassBody>
  
  val String.classParameter: Parameter
  
  val String.loopParameter: Parameter

  val String.destructuringParameter: Parameter
  
  val String.parameterList: ParameterList
  
  val String.typeParameterList: Scope<KtTypeParameterList>
  
  val String.typeParameter: Scope<KtTypeParameter>
  
  val String.lambdaParameterListIfAny: ParameterList
  
  val String.lambdaParameterList: ParameterList
  
  fun lambdaExpression(
    parameters: String,
    body: String
  ): LambdaExpression
  
  val String.enumEntry: Scope<KtEnumEntry>
  
  val enumEntryInitializerList: Scope<KtInitializerList>
  
  val String.whenEntry: WhenEntry
  
  val String.whenCondition: WhenCondition
  
  fun blockStringTemplateEntry(expression: KtExpression): Scope<KtStringTemplateEntryWithExpression>
  
  fun simpleNameStringTemplateEntry(name: String): Scope<KtSimpleNameStringTemplateEntry>
  
  fun literalStringTemplateEntry(literal: String): Scope<KtLiteralStringTemplateEntry>
  
  fun stringTemplate(content: String): Scope<KtStringTemplateExpression>
  
  val String.packageDirective: Scope<KtPackageDirective>

  val String.packageDirectiveOrNull: Scope<KtPackageDirective>
  
  fun importDirective(importPath: ImportPath): ImportDirective
  
  fun primaryConstructor(text: String = ""): Scope<KtPrimaryConstructor>
  
  val primaryConstructorNoArgs: Scope<KtPrimaryConstructor>
  
  fun primaryConstructorWithModifiers(modifiers: String?): Scope<KtPrimaryConstructor>
  
  val constructorKeyword: PsiElement
  
  fun labeledExpression(labelName: String): Scope<KtLabeledExpression>
  
  fun String.typeCodeFragment(context: PsiElement?): Scope<KtTypeCodeFragment>
  
  fun String.expressionCodeFragment(context: PsiElement?): Scope<KtExpressionCodeFragment>
  
  fun String.blockCodeFragment(context: PsiElement?): Scope<KtBlockCodeFragment>
  
  fun `if`(
    condition: KtExpression,
    thenExpr: KtExpression,
    elseExpr: KtExpression? = null
  ): IfExpression
  
  fun argument(
    expression: KtExpression?,
    name: Name? = null,
    isSpread: Boolean = false,
    reformat: Boolean = true
  ): ValueArgument
  
  val String.argument: ValueArgument

  val String.superTypeCallEntry: Scope<KtSuperTypeCallEntry>
  
  val String.superTypeEntry: Scope<KtSuperTypeEntry>
  
  val String.delegatedSuperTypeEntry: Scope<KtConstructorDelegationCall>
  
  val String.block: BlockExpression

  val String.`for`: ForExpression

  val String.`while`: WhileExpression

  val String.`when`: WhenExpression

  val String.`try`: TryExpression

  val String.catch: CatchClause

  val String.finally: FinallySection

  val String.`throw`: ThrowExpression

  val String.`is`: IsExpression

  val String.`if`: IfExpression

  val String.`return`: ReturnExpression

  val String.annotatedExpression: AnnotatedExpression

  val String.functionLiteral: FunctionLiteral

  /**
   * Creates an expression that has reference to its context
   *
   * @param context is used to feed the expression context reference
   */
  fun String.expressionIn(context: PsiElement): Scope<KtExpressionCodeFragment>

  fun singleStatementBlock(
    statement: KtExpression,
    prevComment: String? = null,
    nextComment: String? = null
  ): BlockExpression
  
  val String.comment: PsiComment

  companion object  {
    fun default(project: Project): ElementScope =
      DefaultElementScope(project)
  }
}
