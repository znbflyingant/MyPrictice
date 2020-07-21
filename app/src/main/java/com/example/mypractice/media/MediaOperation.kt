package com.example.mypractice.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import com.example.mypractice.R
import com.example.mypractice.utils.BitmapUtil
import java.io.File


/**
 * 描述:
 * 作者：znb
 * 时间：2019/3/28 13:53
 */
class MediaOperation {
    companion object {
        private val MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC
        val WIDTH = 720
        val HEIGHT = 1280
        val BIT_RATE = WIDTH*HEIGHT*16
        val IFRAME_INTERVAL = 1
        var FRAMES_PER_SECOND = 20
        val VERBOSE = true
        val TAG = "MediaOperation"

    }
    private var mBufferInfo: MediaCodec.BufferInfo?=null
    private var mEncoder: MediaCodec? = null
    private var mMuxer: MediaMuxer? = null
    private var mTrackIndex: Int = 0
    private var mMuxerStarted: Boolean = false
    private var mFakePts: Long = 0
    private var mInputSurface: Surface? = null
    var reuseBitmap:Bitmap?=null
    class TextInfo{
        var sTime = 0L
        var eTime = 0L
        var text = ""
        var color = 0
        var drawableId = 0
    }
    fun getTextInfos():MutableList<TextInfo>{
        val infos = mutableListOf<TextInfo>()
        TextInfo().apply {
            sTime = 0
            eTime = 2000
            color = Color.RED
            drawableId = R.drawable.app_scancode_inner
            text = "A"
        }.let {
            infos.add(it)
        }
        TextInfo().apply {
            text = "B"
            sTime = 2000
            eTime = 5500
            color = Color.GREEN
            drawableId = R.drawable.app_scancode_gray
        }.let {
            infos.add(it)
        }
        TextInfo().apply {
            text = "C"
            sTime = 5500
            eTime = 7000
            color = Color.BLUE
            drawableId = R.drawable.app_scancode_product
        }.let {
            infos.add(it)
        }
        return infos
    }
    fun generate(context:Context,outputFile: File):Boolean {
        var result = true
        try {
            val infos = getTextInfos()
            prepareEncoder(outputFile)
            infos.forEachIndexed { index, textInfo ->
                val dTime = textInfo.eTime-textInfo.sTime
                val frameCount = (dTime.toFloat()/1000*FRAMES_PER_SECOND).toInt()
                Log.d(TAG,"index:$index dTime:$dTime frameCount:$frameCount")
                val paint = Paint()
                paint.color = textInfo.color
                paint.textSize = 18f
                reuseBitmap = (context.resources.getDrawable(textInfo.drawableId) as BitmapDrawable).bitmap
                if (reuseBitmap==null) {
                    reuseBitmap = (context.resources.getDrawable(textInfo.drawableId) as BitmapDrawable).bitmap
                }else{
                    reuseBitmap = BitmapUtil.getBitmap(context,reuseBitmap!!,textInfo.drawableId)
                }
                for (i in 1..frameCount){
                    drainEncoder(false)
                    val canvas = mInputSurface!!.lockCanvas(null)
                    canvas.drawBitmap(reuseBitmap!!,0f,0f,null)
                    canvas.drawText(textInfo.text,0f,0f,paint)
                    mInputSurface!!.unlockCanvasAndPost(canvas)

                }
            }
            drainEncoder(true)
            Log.i(TAG, "Movie generation complete")
        } catch (e: Exception) {
            Log.e(TAG, "Movie generation FAILED", e)
            e.printStackTrace()
            result = false
        } finally {
            try {
                releaseEncoder()
            }catch (e:Exception){
                e.printStackTrace()
                Log.e(TAG, "releaseEncoder FAILED", e)
            }finally {
                return result
            }
        }
    }

    /**
     * Prepares the video encoder, muxer, and an input surface.
     */
    private fun prepareEncoder(outputFile: File) {
        mBufferInfo = MediaCodec.BufferInfo()

        val format = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT)
        Log.d(TAG, "prepareEncoder WIDTH:$WIDTH,HEIGHT:$HEIGHT")
        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)
        mEncoder?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mInputSurface = mEncoder?.createInputSurface()
        mEncoder?.start()

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
            mMuxer = MediaMuxer(outputFile.toString(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mTrackIndex = -1
        mMuxerStarted = false
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    private fun releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects")
        if (mEncoder != null) {
            mEncoder!!.stop()
            mEncoder!!.release()
            mEncoder = null
        }
        if (mInputSurface != null) {
            mInputSurface!!.release()
            mInputSurface = null
        }
        if (mMuxer != null) {
            mMuxer!!.stop()
            mMuxer!!.release()
            mMuxer = null
        }
    }

    /**
     * Extracts all pending data from the encoder.
     *
     *
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private fun drainEncoder(endOfStream: Boolean) {
        if (mBufferInfo==null) {
            return
        }
        val TIMEOUT_USEC = 10000
        if (VERBOSE) Log.d(TAG, "drainEncoder($endOfStream)")

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder")
            mEncoder!!.signalEndOfInputStream()
        }
//        var bufferInfo=MediaCodec.BufferInfo()
//        Log.d(TAG,"encoderOutputBuffers:${encoderOutputBuffers.size}")
        while (true) {
            val outputIndex = mEncoder!!.dequeueOutputBuffer(mBufferInfo!!, TIMEOUT_USEC.toLong())
            Log.d(TAG,"dequeueOutputBuffer,encoderStatus:$outputIndex,mBufferInfo:${mBufferInfo!!.size}")
            if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS")
                }
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val newFormat = mEncoder!!.outputFormat
                Log.d(TAG, "encoder output format changed: $newFormat")

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer!!.addTrack(newFormat)
                mMuxer!!.start()
                mMuxerStarted = true
            } else if (outputIndex < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: $outputIndex")
                // let's ignore it
            } else {
                val encodedData = mEncoder!!.getOutputBuffer(outputIndex)
                        ?: throw RuntimeException("encoderOutputBuffer " + outputIndex +
                                " was null")

                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    mBufferInfo!!.size = 0
                }

                if (mBufferInfo!!.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    // adjust the ByteBuffer values to match BufferInfo
                    encodedData.position(mBufferInfo!!.offset)
                    encodedData.limit(mBufferInfo!!.offset + mBufferInfo!!.size)
                    mBufferInfo!!.presentationTimeUs = mFakePts
                    mFakePts += 1000000L / FRAMES_PER_SECOND
                    mMuxer!!.writeSampleData(mTrackIndex, encodedData, mBufferInfo!!)
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo!!.size + " bytes to muxer mFakePts:$mFakePts")
                }

                mEncoder!!.releaseOutputBuffer(outputIndex, false)

                if (mBufferInfo!!.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly")
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached")
                    }
                    break      // out of while
                }
            }
        }
    }
}