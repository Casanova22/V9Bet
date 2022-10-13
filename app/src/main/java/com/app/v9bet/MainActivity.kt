package com.app.v9bet


import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import model.BoardSize
import model.MemoryGame

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var adapter: BoardAdapter
    private val boardSize: BoardSize = BoardSize.EASY
    private var backToExit = false
    var alertDialog: AlertDialog? = null
    var alertDialog1: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
//        tvNumMoves = findViewById(R.id.tvNumMoves)
//        tvNumPairs = findViewById(R.id.tvNumPairs)

//        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        adapter = BoardAdapter(
            this,
            boardSize,
            memoryGame.cards,
            object : BoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }
            })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())


        createDialog()
        onBackPressed()
    }



    private fun createDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("You Win!")
        alertDialogBuilder.setMessage("Do you want to play again?")
        alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            finish()
        //            memoryGame.restartGame()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { _: DialogInterface, i: Int ->

        }
        alertDialog = alertDialogBuilder.create()
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val alertDialogBuilder1 = AlertDialog.Builder(this)
        alertDialogBuilder1.setTitle("Exit game?")
        alertDialogBuilder1.setMessage("Do you want to exit the game?")
        alertDialogBuilder1.setPositiveButton("Yes") {_: DialogInterface, _: Int ->
            finish()
        }
        alertDialogBuilder1.setNegativeButton("Cancel") { _: DialogInterface, _:Int ->

        }
        alertDialog1 = alertDialogBuilder1.create()
        if (backToExit){
            super.finish()
            return
        }
        this.backToExit = true
        Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show()
        Handler().postDelayed({backToExit = false},2000)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateGameWithFlip(position: Int) {
        // Error checking
        if (memoryGame.haveWonGame()) {
            // alert the user invalid move
            Toast.makeText(applicationContext, "You already won!", LENGTH_LONG ).show()
            alertDialog?.show()
            return
        }
        if (memoryGame.isCardFacedUp(position)) {
            // alert the user invalid move
            Toast.makeText(applicationContext, "Invalid move!", LENGTH_SHORT).show()
            return
        }
        //actually flipping over card
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "found a Match! Num Pairs found: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int

//            tvNumPairs.setTextColor(color)
//            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Toast.makeText(applicationContext, "You Won! Congratulations. Press back to play again!", LENGTH_SHORT).show()
                alertDialog?.show()
            }
//            if (memoryGame.restartGame()){
//                return
//            }
        }
//      tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}