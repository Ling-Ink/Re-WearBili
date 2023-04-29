package cn.spacexc.wearbili.remake.app.main.dynamic.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.spacexc.wearbili.remake.common.UIState
import cn.spacexc.wearbili.remake.common.ui.LoadableBox

/**
 * Created by XC-Qan on 2023/4/27.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DynamicScreen(
    viewModel: DynamicViewModel,
    context: Context
) {
    val dynamicListData = viewModel.dynamicFlow.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = dynamicListData.loadState.refresh is LoadState.Loading,
        onRefresh = { dynamicListData.refresh() },
        refreshThreshold = 40.dp
    )
    LoadableBox(
        uiState = when (dynamicListData.loadState.refresh) {
            is LoadState.Error -> UIState.Failed
            is LoadState.Loading -> UIState.Loading
            else -> UIState.Success
        }, modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state = pullToRefreshState)
        ) {
            LazyColumn(
                state = viewModel.scrollState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = 4.dp,
                    horizontal = /*TitleBackgroundHorizontalPadding*/ 8.dp
                )
            ) {
                items(dynamicListData) {
                    it?.let { dynamicItem ->
                        DynamicCard(item = dynamicItem, context = context)
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = dynamicListData.loadState.refresh is LoadState.Loading,
                state = pullToRefreshState,
                modifier = Modifier.align(
                    Alignment.TopCenter
                )
            )
        }
    }
}