package com.gregantech.timepass.util

import android.content.Context
import com.gregantech.timepass.R
import com.gregantech.timepass.view.tiktok.model.TikTokModel

object DataUtil {

    fun videoList(context: Context, page: Int, limit: Int = 3): ArrayList<TikTokModel> {
        if (page <= 0) {
            throw RuntimeException("Invalid page $page provided. Page must be > 0")
        }
        val vidList = arrayListOf(
            TikTokModel(
                "Instant Upload",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Instant%20Upload.mp4",
                "Jon introduces Instant Upload with a few thoughts on how we remember the things that matter. Check out some ways we've been rethinking real-life sharing for the web at plus.google.com"
            ),
            TikTokModel(
                "New Dad",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20New%20Dad.mp4",
                "With Google+ Instant Upload, every picture you take on your phone is instantly backed up to a private Google+ album. It's a simple way to make sure you never lose another memory."
            ),
            TikTokModel(
                "Say more with Hangouts",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Say%20more%20with%20Hangouts.mp4",
                "Laugh, share news, celebrate, learn something new or stay in touch with Hangouts. And with Hangouts on your phone, you can drop in from wherever you are."
            ),
            TikTokModel(
                "Google+ Search",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Search.mp4",
                "Search on Google+ helps you get advice from the people you know -- sometimes when you least expect it. Check out some ways we've been rethinking real-life sharing for the web at plus.google.com."
            ),
            TikTokModel(
                "Sharing but like real life",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Sharing%20but%20like%20real%20life.mp4",
                context.getString(R.string.dummy_6)
            ),
            TikTokModel(
                "Google+ Circles",
                "http://www.exit109.com/~dnn/clips/RW20seconds_1.mp4",
                "New ways of sharing the right things with the right people. Join at http://google.com/+"
            ),
            TikTokModel(
                "Google+ Hangouts",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Circles.mp4",
                "Jed introduces Circles with a few thoughts on the nature of friendship. Check out some ways we've been rethinking real-life sharing for the web at plus.google.com."
            ),
            TikTokModel(
                "20ft Search",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Google%2B/Google%2B_%20Hangouts.mp4",
                "Aimee introduces Hangouts with a few thoughts on the spontaneous get-together. Check out some ways we've been rethinking real-life sharing for the web at plus.google.com."
            ),
            TikTokModel(
                "Balcony Toss",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Dance Search",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Balcony%20Toss.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Epic Docs Animation",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Dance%20Search.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Extra Spicy",
                "https://adroitsolutionz.com/video-test.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Get Your Money's Worth",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Epic%20Docs%20Animation.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Guitar Search",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Extra%20Spicy.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Hangin' with the Google Search Bar",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Get%20Your%20Money's%20Worth.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Hometown Caroling",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Guitar%20Search.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            ),
            TikTokModel(
                "Instant Music",
                "https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Hangin'%20with%20the%20Google%20Search%20Bar.mp4",
                "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
            )
        )
        val newList = ArrayList<TikTokModel>()
        for (i in ((page - 1) * limit) until page * limit) {
            newList.add(vidList[i])
        }
        return newList
    }

}