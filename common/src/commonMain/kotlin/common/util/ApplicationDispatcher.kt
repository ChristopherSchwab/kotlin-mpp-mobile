package common.util

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Expects a CoroutineDispatcher from all platforms, defining how asynchronous tasks should be executed.
 */
internal expect val ApplicationDispatcher: CoroutineDispatcher