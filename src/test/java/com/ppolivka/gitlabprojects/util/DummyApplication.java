package com.ppolivka.gitlabprojects.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ModalityInvokator;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Dummy implementation of intellij Application to mock Service Manager
 *
 * @author ppolivka
 * @since 1.3.6
 */
public class DummyApplication implements Application {

  private Object service;

  public DummyApplication(Object service) {
    this.service = service;
  }

  @Override
  public void invokeLaterOnWriteThread(@NotNull final Runnable action) {

  }

  @Override
  public void invokeLaterOnWriteThread(@NotNull final Runnable action, @NotNull final ModalityState modal) {

  }

  @Override
  public void invokeLaterOnWriteThread(@NotNull final Runnable action, @NotNull final ModalityState modal, @NotNull final Condition<?> expired) {

  }

  @Override
  public void runReadAction(@NotNull Runnable runnable) {

  }

  @Override
  public <T> T runReadAction(@NotNull Computable<T> computable) {
    return null;
  }

  @Override
  public <T, E extends Throwable> T runReadAction(@NotNull ThrowableComputable<T, E> throwableComputable) throws E {
    return null;
  }

  @Override
  public void runWriteAction(@NotNull Runnable runnable) {

  }

  @Override
  public <T> T runWriteAction(@NotNull Computable<T> computable) {
    return null;
  }

  @Override
  public <T, E extends Throwable> T runWriteAction(@NotNull ThrowableComputable<T, E> throwableComputable) throws E {
    return null;
  }

  @Override
  public boolean hasWriteAction(@NotNull Class<?> aClass) {
    return false;
  }

  @Override
  public void assertReadAccessAllowed() {

  }

  @Override
  public void assertWriteAccessAllowed() {

  }

  @Override
  public void assertIsDispatchThread() {

  }

  @Override
  public void assertIsNonDispatchThread() {

  }

  @Override
  public void assertIsWriteThread() {

  }

  @Override
  public void addApplicationListener(@NotNull ApplicationListener applicationListener) {

  }

  @Override
  public void addApplicationListener(@NotNull ApplicationListener applicationListener, @NotNull Disposable disposable) {

  }

  @Override
  public void removeApplicationListener(@NotNull ApplicationListener applicationListener) {

  }

  @Override
  public void saveAll() {

  }

  @Override
  public void saveSettings() {

  }

  @Override
  public void exit() {

  }

  @Override
  public boolean isWriteAccessAllowed() {
    return false;
  }

  @Override
  public boolean isReadAccessAllowed() {
    return false;
  }

  @Override
  public boolean isDispatchThread() {
    return false;
  }

  @Override
  public boolean isWriteThread() {
    return false;
  }

  @NotNull
  @Override
  public ModalityInvokator getInvokator() {
    return null;
  }

  @Override
  public void invokeLater(@NotNull Runnable runnable) {

  }

  @Override
  public void invokeLater(@NotNull Runnable runnable, @NotNull Condition condition) {

  }

  @Override
  public void invokeLater(@NotNull Runnable runnable, @NotNull ModalityState modalityState) {

  }

  @Override
  public void invokeLater(@NotNull Runnable runnable, @NotNull ModalityState modalityState, @NotNull Condition condition) {

  }

  @Override
  public void invokeAndWait(@NotNull Runnable runnable, @NotNull ModalityState modalityState) throws ProcessCanceledException {

  }

  @Override
  public void invokeAndWait(@NotNull Runnable runnable) throws ProcessCanceledException {

  }

  @NotNull
  @Override
  public ModalityState getCurrentModalityState() {
    return null;
  }

  @NotNull
  @Override
  public ModalityState getModalityStateForComponent(@NotNull Component component) {
    return null;
  }

  @NotNull
  @Override
  public ModalityState getDefaultModalityState() {
    return null;
  }

  @NotNull
  @Override
  public ModalityState getNoneModalityState() {
    return null;
  }

  @NotNull
  @Override
  public ModalityState getAnyModalityState() {
    return null;
  }

  @Override
  public long getStartTime() {
    return 0;
  }

  @Override
  public long getIdleTime() {
    return 0;
  }

  @Override
  public boolean isUnitTestMode() {
    return false;
  }

  @Override
  public boolean isHeadlessEnvironment() {
    return false;
  }

  @Override
  public boolean isCommandLine() {
    return false;
  }

  @NotNull
  @Override
  public Future<?> executeOnPooledThread(@NotNull Runnable runnable) {
    return null;
  }

  @NotNull
  @Override
  public <T> Future<T> executeOnPooledThread(@NotNull Callable<T> callable) {
    return null;
  }

  @Override
  public boolean isDisposeInProgress() {
    return false;
  }

  @Override
  public boolean isRestartCapable() {
    return false;
  }

  @Override
  public void restart() {

  }

  @Override
  public boolean isActive() {
    return false;
  }

  @NotNull
  @Override
  public AccessToken acquireReadActionLock() {
    return null;
  }

  @NotNull
  @Override
  public AccessToken acquireWriteActionLock(@NotNull Class aClass) {
    return null;
  }

  @Override
  public boolean isInternal() {
    return false;
  }

  @Override
  public boolean isEAP() {
    return false;
  }

  @Override
  public BaseComponent getComponent(@NotNull String s) {
    return null;
  }

  @Override
  public <T> T getComponent(@NotNull Class<T> aClass) {
    return null;
  }

  @Override
  public <T> T getComponent(@NotNull Class<T> aClass, T t) {
    return null;
  }

  @Override
  public boolean hasComponent(@NotNull Class aClass) {
    return false;
  }

  @NotNull
  @Override
  public <T> T[] getComponents(@NotNull Class<T> aClass) {
    return null;
  }

  @NotNull
  @Override
  public PicoContainer getPicoContainer() {
    return new PicoContainer() {
      @Override
      public Object getComponentInstance(Object o) {
        return service;
      }

      @Override
      public Object getComponentInstanceOfType(Class aClass) {
        return null;
      }

      @Override
      public ComponentAdapter getComponentAdapter(Object o) {
        return null;
      }

    };
  }

  @NotNull
  @Override
  public MessageBus getMessageBus() {
    return null;
  }

  @Override
  public boolean isDisposed() {
    return false;
  }

  @NotNull
  @Override
  public <T> T[] getExtensions(@NotNull ExtensionPointName<T> extensionPointName) {
    return null;
  }

  @NotNull
  @Override
  public Condition getDisposed() {
    return null;
  }

  @Override
  public @NotNull RuntimeException createError(@NotNull final Throwable error, @NotNull final PluginId pluginId) {
    return null;
  }

  @Override
  public @NotNull RuntimeException createError(@NotNull @NonNls final String message, @NotNull final PluginId pluginId) {
    return null;
  }

  @Override
  public @NotNull RuntimeException createError(@NotNull @NonNls final String message, @NotNull final PluginId pluginId, @Nullable final Map<String, String> attachments) {
    return null;
  }

  @Override
  public @NotNull <T> Class<T> loadClass(@NotNull final String className, @NotNull final PluginDescriptor pluginDescriptor) throws ClassNotFoundException {
    return null;
  }

  @Override
  public void dispose() {

  }

  @Nullable
  @Override
  public <T> T getUserData(@NotNull Key<T> key) {
    return null;
  }

  @Override
  public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

  }
}
