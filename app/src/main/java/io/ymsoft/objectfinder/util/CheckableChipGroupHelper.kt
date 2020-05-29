package io.ymsoft.objectfinder.util

import android.annotation.SuppressLint
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.get
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.common.OnModelClickListener

/**체크 가능한 ChipGroup을 다루기 편하게 도와주는 Helper
 * 그룹의 체크 가능 상태를 리스너를 통해 전달받을 수 있다.
 * 그룹의 체크된 모델의 수를 리스너를 통해 전달받을 수 있다.
 * 그룹의 체크된 목록의 리스트를 반환할 수 있다.
 * Chip을 롱클릭시 전체 Chip들이 체크 가능한 상태로 돌입한다.
 * 체크 가능한 상태에서 다시 체크 불가능한 상태로 만들기 위해서는 함수를 호출해야한다.
 * */
class CheckableChipGroupHelper<T : CheckableChipGroupHelper.ChipModel> {
    lateinit var chipGroup: ChipGroup
    private val context by lazy { chipGroup.context }


    var chipClickListener : OnModelClickListener<T>? = null
    var checkableChangeListener: OnCheckableChangeListener? = null  // chipGroup의 체크 가능 여부를 전달
    var checkedCounterChangeListener: OnCheckedCounterChangeListener? = null    // 체크된 모델의 수 전달

    // Chip들을 체크 가능한 상태 여부
    private var isCheckable = false

    // 체크된 Chip 수
    private var checkedCount = 0

    // 현재 Chip으로 생성되어있는 Model 리스트
    private var currentList = listOf<T>()

    // 현재 체크된 Model 리스트
    private var checkedModelsMap = hashMapOf<Long, T>()

    /**모델 리스트를 Chip으로 생성한다.
     * 기존의 리스트와 비교하여 새롭게 추가된 경우에는 새롭게 추가된 모델만 Chip으로 추가한다.
     * 기존의 리스트와 비교하여 모델이 삭제된 경우에는 chipGroup을 clear 하고 다시 Chip들을 추가한다.*/
    @SuppressLint("CheckResult")
    fun setChipGroups(list: List<T>) {
        val oldItemCount = currentList.size
        if (oldItemCount != 0 && list.size - oldItemCount == 1) {
            //ObjectModel을 추가하여 새롭게 갱신할 리스트가 기존의 리스트보다 1개 많은 경우 전체를 갱신하지 않고 Chip을 하나만 추가한다.
            val model = list[list.lastIndex]
            val chip = Chip(context)
            chip.text = model.modelName
            chip.tag = model
            chip.isCheckable = false
            chip.setOnClickListener(clickListener)
            chip.setOnLongClickListener(chipLongClickListener)
            chip.setOnCheckedChangeListener(chipCheckedChangeListener)
            chipGroup.addView(chip)
            chip.animateFadeIn()
            logI("${model.modelName} 추가!")
            currentList = list
        } else {
            //ObjectModel을 1개 추가한 경우가 아닐때 : 삭제 혹은 최초 로딩
            Observable.just(list)
                .map {
                    val chipList = arrayListOf<Chip>()
                    list.forEach {
                        val chip = Chip(context)
                        chip.text = it.modelName
                        chip.tag = it
                        chip.isCheckable = isCheckable

                        chip.setOnClickListener(clickListener)
                        chip.setOnLongClickListener(chipLongClickListener)
                        chip.setOnCheckedChangeListener(chipCheckedChangeListener)
                        chipList.add(chip)
                    }
                    chipList
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { chipList ->
                    chipGroup.removeAllViews()
                    chipList.forEach(chipGroup::addView)
                    if(list.size > currentList.size)
                        chipGroup.animateFadeIn(200)    // 최초 로딩시에만 애니메이션 적용

                    currentList = list
                    // 이곳이 실행되는 경우는 체크한 항목들이 삭제되거나 이동된 경우이므로 체크 기능 false 로 초기화
                    setCheckable(false)
                }
        }
    }

    /**그룹의 체크 가능 여부를 변경한다. */
    fun setCheckable(newState: Boolean) {
        if (newState != isCheckable) {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup[i] as Chip
                chip.isCheckable = newState
            }
            setCheckedCount(0)

            isCheckable = newState
            checkableChangeListener?.onChanged(isCheckable)
        }
    }

    /**모든 Chip들을 체크하거나 체크해제한다. */
    fun checkAllChips(checked: Boolean) {
        if (checked) {
            val chipCount = chipGroup.childCount
            for (i in 0 until chipCount) {
                val chip = chipGroup[i] as Chip
                chip.isChecked = checked
            }
            currentList.forEach {
                checkedModelsMap[it.modelId] = it
            }
            setCheckedCount(chipCount)
        } else {
            checkedModelsMap.clear()
            chipGroup.clearCheck()
            setCheckedCount(0)
        }

    }


    private val clickListener = View.OnClickListener {
        if(!isCheckable){
            val model = it.tag as T
            chipClickListener?.onItemClick(model)
        }

    }

    private val chipLongClickListener = View.OnLongClickListener {
//        val model = it.tag as T
        val chip = it as Chip
        setCheckable(true)
        chip.isChecked = true
        true
    }

    private val chipCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { it, isChecked ->
            val model = it.tag as T

            if (isChecked) {
                checkedModelsMap[model.modelId] = model
                addCheckedCount(1)
            } else {
                checkedModelsMap.remove(model.modelId)
                addCheckedCount(-1)
            }
        }

    private fun setCheckedCount(count: Int) {
        checkedCount = count
        checkedCounterChangeListener?.onChanged(checkedCount, currentList.size)
    }

    private fun addCheckedCount(count: Int) {
        checkedCount += count
        checkedCounterChangeListener?.onChanged(checkedCount, currentList.size)
    }

    fun getCheckedList(): List<T> {
        return checkedModelsMap.values.toList()
    }

    fun clear() {
        // Chip들을 체크 가능한 상태 여부
//        isCheckable = false

        // 체크된 Chip 수
//        checkedCount = 0

        // 현재 Chip으로 생성되어있는 Model 리스트
        currentList = listOf<T>()

        // 현재 체크된 Model 리스트
        checkedModelsMap.clear()
    }


    interface OnCheckableChangeListener {
        /**기존의 체커블 상태와 달라졌을경우 호출된다.*/
        fun onChanged(checkable: Boolean)
    }

    interface OnCheckedCounterChangeListener {
        /**체크된 Chip의 수가 변경될때마다 호출된다.
         * @param total Chip 천체 갯수*/
        fun onChanged(count: Int, total: Int)
    }


    interface ChipModel {
        val modelId : Long
        val modelName : String
    }
}