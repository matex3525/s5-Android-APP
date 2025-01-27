package com.example.s5app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.event.AlbumImageDetailsScreenEvent
import com.example.s5app.model.AlbumImage
import com.example.s5app.model.ImageComment
import com.example.s5app.network.AddCommentToPhotoRequest
import com.example.s5app.network.ApiResult
import com.example.s5app.use_case.EventUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltViewModel(assistedFactory = AlbumImageDetailsScreenViewModel.AlbumImageDetailsScreenViewModelFactory::class)
class AlbumImageDetailsScreenViewModel @AssistedInject constructor (
    private val eventUseCases: EventUseCases,
    @Assisted bitmapSource: ImageBitmap,
    @Assisted("userToken") private val userToken: String,
    @Assisted("imageId") private val imageId: String,
    @Assisted("adminToken") private val adminToken: String?
) : ViewModel() {

    @AssistedFactory
    interface AlbumImageDetailsScreenViewModelFactory {
        fun create(bitmapSource: ImageBitmap, @Assisted("userToken") userToken: String, @Assisted("imageId") imageId: String, @Assisted("adminToken") adminToken: String?): AlbumImageDetailsScreenViewModel
    }

    private val _bitmapSource = mutableStateOf(bitmapSource)
    val bitmapSource: State<ImageBitmap> = _bitmapSource

    private val _comments: SnapshotStateList<ImageComment> = mutableStateListOf()
    val comments: State<List<ImageComment>> = mutableStateOf(_comments)

    init {
        onEvent(AlbumImageDetailsScreenEvent.GetAllComments)
    }

    private fun getCommentData(text: String): AddCommentToPhotoRequest {
        return AddCommentToPhotoRequest(text)
    }

    fun onEvent(event: AlbumImageDetailsScreenEvent) {
        when (event) {
            is AlbumImageDetailsScreenEvent.GetAllComments -> {
                viewModelScope.launch {
                    val result = eventUseCases.getCommentsForGivenPhoto(userToken, imageId, 0, -1)
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            _comments.clear()
                            _comments.addAll(result.data.map { ImageComment.fromGetGivenPhotoCommentParams(it) })
                        }
                    }
                }
            }
            is AlbumImageDetailsScreenEvent.PostComment -> {
                viewModelScope.launch {
                    val commentData = getCommentData(event.comment)
                    val result = eventUseCases.addCommentToPhoto(userToken, imageId, commentData)
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            val data = result.data
                            _comments.add(ImageComment(data.commentId, event.comment, data.time))
                        }
                    }
                }
            }
            is AlbumImageDetailsScreenEvent.DeleteComment -> {
                viewModelScope.launch {
                    if (adminToken == null) return@launch
                    eventUseCases.deleteCommentFromPhoto(userToken, imageId, event.commentId, adminToken)
                    withContext(Dispatchers.Main) {
                        _comments.removeIf {it.commentId == event.commentId}
                    }
                }
            }
        }
    }
}