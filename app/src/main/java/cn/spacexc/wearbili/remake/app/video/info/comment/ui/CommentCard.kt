package cn.spacexc.wearbili.remake.app.video.info.comment.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import cn.spacexc.wearbili.remake.R
import cn.spacexc.wearbili.remake.app.TAG
import cn.spacexc.wearbili.remake.app.video.info.comment.domain.CommentContentData
import cn.spacexc.wearbili.remake.app.video.info.comment.domain.EmoteObject
import cn.spacexc.wearbili.remake.app.video.info.ui.PARAM_VIDEO_ID
import cn.spacexc.wearbili.remake.app.video.info.ui.PARAM_VIDEO_ID_TYPE
import cn.spacexc.wearbili.remake.app.video.info.ui.VIDEO_TYPE_AID
import cn.spacexc.wearbili.remake.app.video.info.ui.VideoInformationActivity
import cn.spacexc.wearbili.remake.common.domain.color.parseColor
import cn.spacexc.wearbili.remake.common.domain.time.toDateStr
import cn.spacexc.wearbili.remake.common.domain.video.VideoUtils
import cn.spacexc.wearbili.remake.common.domain.video.toShortChinese
import cn.spacexc.wearbili.remake.common.ui.BilibiliPink
import cn.spacexc.wearbili.remake.common.ui.ClickableText
import cn.spacexc.wearbili.remake.common.ui.SmallUserCard
import cn.spacexc.wearbili.remake.common.ui.clickVfx
import cn.spacexc.wearbili.remake.common.ui.spx
import cn.spacexc.wearbili.remake.common.ui.theme.AppTheme
import cn.spacexc.wearbili.remake.common.ui.theme.wearbiliFontFamily
import cn.spacexc.wearbili.remake.common.ui.toOfficialVerify
import coil.compose.AsyncImage
import coil.request.ImageRequest

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Composable
fun RichText(
    isCommentReply: Boolean = false,
    replyUserName: AnnotatedString = buildAnnotatedString { },
    replyUserMid: Long = 0L,
    isTopComment: Boolean,
    origText: String,
    emoteMap: Map<String, EmoteObject>,
    jumpUrlMap: Map<String, CommentContentData.JumpUrlObject>,
    attentionUserMap: Map<String, Long>,
    fontSize: TextUnit,
    context: Context,
    maxLines: Int = Int.MAX_VALUE,
    onGloballyClicked: () -> Unit
) {
    val inlineTextContent = hashMapOf(
        "topCommentLabel" to InlineTextContent(
            placeholder = Placeholder(
                width = 20.spx * 1.1f,
                height = 12.5.spx * 1.1f,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            ),
        ) {
            Text(
                text = "置顶",
                fontSize = 8.spx,
                fontFamily = wearbiliFontFamily,
                color = Color.White,
                modifier = Modifier
                    .padding(end = 1.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BilibiliPink)
                    .fillMaxSize()
                    .offset(y = (1).dp)
                /*.padding(
                    start = 11.dp,
                    end = 10.5.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )*/,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    )
    var temp = origText
    jumpUrlMap.forEach {
        temp = temp.replace(it.key, "///${it.key}///")
    }
    attentionUserMap.forEach {
        temp = temp.replace("@${it.key}", "///${it.key}///")
    }
    val list = temp.replace("[", "///").replace("]", "///").split("///")
    val annotatedString = buildAnnotatedString {
        if (isTopComment) {
            appendInlineContent("topCommentLabel")
        }
        if (isCommentReply) {
            if (replyUserMid != 0L) {
                pushStringAnnotation(
                    tag = "tagUser",
                    annotation = replyUserMid.toString()
                )
            }
            append(replyUserName)
            if (replyUserMid != 0L) {
                pop()
            }
        }
        list.forEach {
            if (emoteMap.containsKey("[$it]")) {
                appendInlineContent(id = "[$it]")
                inlineTextContent["[$it]"] = InlineTextContent(
                    placeholder = Placeholder(
                        width = fontSize.times(1.4f),
                        height = fontSize.times(1.4f),
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) { _ ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(emoteMap["[$it]"]?.url)/*.size(
                                with(localDensity) { fontSize.roundToPx() }
                            )*/.crossfade(true).build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else if (jumpUrlMap.containsKey(it)) {
                if (jumpUrlMap[it]?.extra?.is_word_search == true) {
                    pushStringAnnotation(
                        tag = "tagSearch",
                        annotation = jumpUrlMap[it]?.title ?: ""
                    )
                } else {
                    pushStringAnnotation(tag = "tagUrl", annotation = it)
                }
                if (jumpUrlMap[it]?.extra?.is_word_search == true) {
                    withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                        append(jumpUrlMap[it]?.title ?: "")
                    }
                }
                if (!jumpUrlMap[it]?.prefix_icon.isNullOrEmpty()) {
                    appendInlineContent(id = jumpUrlMap[it]?.prefix_icon ?: "")
                    inlineTextContent[jumpUrlMap[it]?.prefix_icon ?: ""] = InlineTextContent(
                        placeholder = Placeholder(
                            width = fontSize.times(1.3f),
                            height = fontSize.times(1.3f),
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) { _ ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(jumpUrlMap[it]?.prefix_icon)/*.size(
                                with(localDensity) { fontSize.roundToPx() }
                            )*/.crossfade(true).build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                if (jumpUrlMap[it]?.extra?.is_word_search != true) {
                    withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                        append(jumpUrlMap[it]?.title ?: "")
                    }
                }
                pop()
            } else if (attentionUserMap.containsKey(it)) {
                pushStringAnnotation(
                    tag = "tagUser",
                    annotation = attentionUserMap[it]?.toString() ?: ""
                )
                withStyle(style = SpanStyle(color = parseColor("#008ac5"))) {
                    append("@$it")
                }
                pop()
            } else {
                withStyle(
                    style = if (isCommentReply) SpanStyle(
                        color = Color(
                            255,
                            255,
                            255,
                            179
                        )
                    ) else SpanStyle()
                ) {
                    append(it)
                }
            }
        }
    }
    ClickableText(
        text = annotatedString,
        onClick = { index ->
            annotatedString.getStringAnnotations(tag = "tagUrl", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    if (VideoUtils.isBV(annotation.item)) {
                        Intent(context, VideoInformationActivity::class.java).apply {
                            putExtra(PARAM_VIDEO_ID, annotation.item)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (VideoUtils.isAV(annotation.item)) {
                        Intent(context, VideoInformationActivity::class.java).apply {
                            putExtra(PARAM_VIDEO_ID, annotation.item)
                            putExtra(PARAM_VIDEO_ID_TYPE, VIDEO_TYPE_AID)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } /*else if (annotation.item.startsWith("http")) {
                        context.startActivity(
                            Intent(
                                context,
                                LinkProcessActivity::class.java
                            ).apply {
                                putExtra("url", annotation.item)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                    }*/
                    return@ClickableText
                }
            /*annotatedString.getStringAnnotations(tag = "tagUser", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    Log.d(TAG, "RichText: tagUser: ${annotation.item}")
                    context.startActivity(Intent(context, SpaceProfileActivity::class.java).apply {
                        putExtra("userMid", annotation.item.toLong())
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    return@ClickableText
                }
            annotatedString.getStringAnnotations(tag = "tagUrl", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    if (VideoUtils.isBV(annotation.item)) {
                        Intent(context, VideoActivity::class.java).apply {
                            putExtra("videoId", annotation.item)
                            putExtra("videoIdType", VIDEO_ID_BV)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (VideoUtils.isAV(annotation.item)) {
                        Intent(context, VideoActivity::class.java).apply {
                            putExtra("videoId", VideoUtils.av2bv(annotation.item))
                            putExtra("videoIdType", VIDEO_ID_BV)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    } else if (annotation.item.startsWith("http")) {
                        context.startActivity(
                            Intent(
                                context,
                                LinkProcessActivity::class.java
                            ).apply {
                                putExtra("url", annotation.item)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                    }
                    return@ClickableText
                }
            annotatedString.getStringAnnotations(tag = "tagSearch", start = index, end = index)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(context, SearchResultActivityNew::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("keyword", annotation.item.log("keyword"))
                    context.startActivity(intent)
                    return@ClickableText
                }*/
            Log.d(TAG, "RichText: Global Click Event")
            onGloballyClicked()
        },
        style = AppTheme.typography.body1.copy(fontSize = fontSize),
        inlineTextContent = inlineTextContent,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun CommentCard(
    senderName: String,
    senderNameColor: String,
    senderAvatar: String,
    senderPendant: String,
    senderOfficialVerify: Int,
    senderMid: Long,
    sendTimeStamp: Long,
    senderIpLocation: String,
    commentContent: String,
    commentLikeCount: Int,
    commentRepliesCount: Int,
    commentReplies: List<CommentContentData.Replies>,
    commentReplyControl: String,
    commentRpid: Long,
    commentEmoteMap: Map<String, EmoteObject>,
    commentAttentionedUsersMap: Map<String, Long>,
    commentJumpUrlMap: Map<String, CommentContentData.JumpUrlObject>,
    isUpLiked: Boolean,
    isTopComment: Boolean,
    uploaderMid: Long,
    context: Context,
    isClickable: Boolean,
    oid: Long,
) {
    Column(
        modifier = Modifier
            .clickVfx(isEnabled = isClickable) {
                /*if (isClickable) {
                    Intent(context, CommentRepliesActivity::class.java).apply {
                        putExtra("oid", oid)
                        putExtra("rootCommentId", commentRpid)
                        putExtra("upMid", uploaderMid)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }*/
            }
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        val localDensity = LocalDensity.current
        val uploaderLabelInlineTextContent = mapOf(
            "uploaderLabel" to InlineTextContent(
                placeholder = Placeholder(
                    width = 18.spx,
                    height = 12.5.spx,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                ),
            ) {
                Text(
                    text = "UP",
                    fontSize = 8.spx,
                    fontFamily = wearbiliFontFamily,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(BilibiliPink)
                        .fillMaxSize()
                        .offset(y = (1).dp)
                    /*.padding(
                        start = 11.dp,
                        end = 10.5.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    )*/,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        )
        SmallUserCard(
            avatar = senderAvatar,
            username = senderName,
            usernameColor = senderNameColor,
            pendant = senderPendant,
            officialVerify = senderOfficialVerify.toOfficialVerify(),
            userInfo = buildString {
                append(
                    sendTimeStamp.toDateStr("yyyy-MM-dd")
                )
                if (senderIpLocation.isNotEmpty()) append("\n$senderIpLocation")

            },
            userLabel = buildAnnotatedString {
                if (senderMid == uploaderMid) {
                    appendInlineContent("uploaderLabel")
                }
            },
            inlineContent = uploaderLabelInlineTextContent
        )
        Column(Modifier.padding(horizontal = 6.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            RichText(
                isTopComment = isTopComment,
                origText = commentContent,
                emoteMap = commentEmoteMap,
                jumpUrlMap = commentJumpUrlMap,
                attentionUserMap = commentAttentionedUsersMap,
                fontSize = AppTheme.typography.body1.fontSize * 1.1f,
                context = context
            ) {
                /*if (isClickable) {
                    Intent(context, CommentRepliesActivity::class.java).apply {
                        putExtra("oid", oid)
                        putExtra("rootCommentId", commentRpid)
                        putExtra("upMid", uploaderMid)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }*/
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                CommentInfoItem(
                    icon = Icons.Outlined.ThumbUp,
                    content = commentLikeCount.toShortChinese()
                )
                Spacer(modifier = Modifier.width(4.dp))
                CommentInfoItem(icon = Icons.Outlined.ThumbDown, content = "")
                Spacer(modifier = Modifier.weight(1f))
                CommentInfoItem(
                    icon = painterResource(id = R.drawable.icon_comment_reply),
                    content = "回复(${commentRepliesCount.toShortChinese()})"
                )
            }
            if (isUpLiked) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(100)
                        )
                        .background(BilibiliPink)
                        .padding(
                            start = 11.dp,
                            end = 10.5.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ), verticalAlignment = Alignment.CenterVertically
                ) {
                    var textHeight by remember {
                        mutableStateOf(0.dp)
                    }
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier
                            .size(textHeight),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "UP主觉得很赞",
                        fontSize = 9.5.spx,
                        fontFamily = wearbiliFontFamily,
                        color = Color.White,
                        modifier = Modifier
                            .onGloballyPositioned {
                                textHeight =
                                    with(localDensity) { it.size.height.toDp() }
                            },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (commentReplies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            parseColor("#262626")
                        )
                        .padding(8.dp)
                ) {
                    commentReplies.forEach {
                        RichText(
                            isTopComment = false,
                            origText = it.content?.message ?: "",
                            emoteMap = it.content?.emote ?: emptyMap(),
                            jumpUrlMap = it.content?.jump_url ?: emptyMap(),
                            attentionUserMap = it.content?.at_name_to_mid ?: emptyMap(),
                            fontSize = AppTheme.typography.body1.fontSize,
                            isCommentReply = true,
                            context = context,
                            replyUserName = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = if (it.member.mid == uploaderMid) BilibiliPink else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(it.member.uname)
                                }
                                append(": ")
                            },
                            replyUserMid = it.member.mid,
                            maxLines = 2
                        ) {
                            /*if (isClickable) {
                                Intent(context, CommentRepliesActivity::class.java).apply {
                                    putExtra("oid", oid)
                                    putExtra("rootCommentId", commentRpid)
                                    putExtra("upMid", uploaderMid)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(this)
                                }
                            }*/
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var textHeight by remember {
                            mutableStateOf(0.dp)
                        }
                        Text(
                            text = commentReplyControl,
                            fontSize = 9.spx,
                            fontFamily = wearbiliFontFamily,
                            color = BilibiliPink,
                            modifier = Modifier
                                .onGloballyPositioned {
                                    textHeight =
                                        with(localDensity) { it.size.height.toDp() }
                                },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier
                                .size(textHeight - 4.dp)
                                .offset(y = (0.5).dp),
                            tint = BilibiliPink
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentInfoItem(
    icon: ImageVector,
    content: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val localDensity = LocalDensity.current
    Row(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
    }, verticalAlignment = Alignment.CenterVertically) {
        var textHeight by remember {
            mutableStateOf(0.dp)
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .alpha(0.6f)
                .size(textHeight),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(text = content,
            fontSize = 10.spx,
            fontFamily = wearbiliFontFamily,
            color = Color.White,
            modifier = Modifier
                .alpha(0.6f)
                .onGloballyPositioned {
                    textHeight =
                        with(localDensity) { it.size.height.toDp() }
                }
        )
    }
}

@Composable
fun CommentInfoItem(
    icon: Painter,
    content: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val localDensity = LocalDensity.current
    Row(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
    }, verticalAlignment = Alignment.CenterVertically) {
        var textHeight by remember {
            mutableStateOf(0.dp)
        }
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .alpha(0.6f)
                .size(textHeight),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = content,
            fontSize = 10.spx,
            fontFamily = wearbiliFontFamily,
            color = Color.White,
            modifier = Modifier
                .alpha(0.6f)
                .onGloballyPositioned {
                    textHeight =
                        with(localDensity) { it.size.height.toDp() }
                }
        )
    }
}