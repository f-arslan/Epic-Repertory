package com.example.epic.data

import com.example.epic.FirebaseOperations


class DataSource {
    val musicList: MutableList<Music> = mutableListOf()
    val commentList: MutableList<Comment> = mutableListOf()


    fun loadMusics(): MutableList<Music> {
        musicList.addAll(mutableListOf(
            Music(
                "1",
                "Her Şeyi Yak",
                "Duman",
                "duman_icon",
                "Beni yak kendini yak her şeyi yak\nBir kıvılcım yeter ben hazırım bak\nİster öp okşa istersen öldür\nAşk için ölmeli aşk o zaman aşk\nAşk için ölmeli aşk o zaman aşk\nSeni içime çektim bir nefeste\nYüreğim tutuklu göğsüm kafeste\nYanacağız ikimiz de ateşte\nBir kıvılcım yeter hazırım bak\nAşk için ölmeli aşk o zaman aşk\nAllah'ım Allah'ım ateşlere yürüyorum\nAllah'ım acı ile aşk ile büyüyorum\nBeni yor hasretinle sevginle yor\nSevgisizlik ayrılıktan daha zor\nDilediğin kadar acıt canımı\nVarlığın da yokluğun da yetmiyor\nVarlığın da yokluğun da yetmiyor",
                "Cm                          A#    G#\nCm                          A#   G#\nG                    G#\nFm                        G\nFm                      G#       Cm\nCm                       A#  G#\nCm                         A#       G#\nG                G#\nFm                      G\nFm                       G#       Cm\nCm Fm  G#     G  Fm          Cm\nCm   Fm G#  G   Fm          Cm\nCm                        A#      G#\nCm                         A# G#\nG                  G#\nFm                     G\nFm                      G#"

            ),
            Music(
                "2",
                "Kusura Bakma",
                "Tuğkan",
                "tugkan",
                "Birden, geldin aklımdan içimden\nKalbimde bitmeyen bir parça en temiz yerinden\nSahiden bekleyen en aptal halime gülen\nSana kızgın sana hasret yine ben, yine ben\nNeden   bilmem\nKusura bakma seni unutamadım\nBu benim hatam ne yapsam olduramadım\nAlev  alev yanıyor can kafesim\nKesilir nefesim seni bırakamadım\nKusura bakma seni unutamadım\nBu benim hatam ne yapsam olduramadım\nAlev  alev yanıyor can kafesim\nKesilir nefesim seni unutamadım\nNe yapsam olduramadım\nSeni bırakamadım"
            ),
            Music(
                "3",
                "Gülpembe",
                "Barış Manço",
                "baris",
                "Sen gülünce güller açar Gülpembe\nBülbüller seni söyler, biz dinlerdik Gülpembe\nSen gelince bahar gelir Gülpembe\nDereler seni çaglar, sevinirdik Gülpembe\nGüz yağmurlarıyla, birgün göçtün gittin\nİnanamadik Gülpembe\nBizim iller sessiz, bizim iller sensiz\nOlamadı Gülpembe\nDudağımda son bir türkü Gülpembe\nHala hep seni söyler, seni çağırır Gülpembe\nDudağımda son bir türkü Gülpembe\nHala hep seni söyler, seni çağırır Gülpembe\nGözlerimde son bir umut Gülpembe\nHala hep seni arar, seni bekler Gülpembe"
            ),
            Music(
                "4",
                "Gesibağları",
                "Barış Manço",
                "baris2",
                "Gesi bağlarında dolanıyorum\nYitirdim yarimi, aman aranıyorum\nYitirdim yarimi, aman aranıyorum\nBir tek selamına güveniyorum\nGel otur yanıma, hallarımı söyleyim\nDerdimden anlamaz, ben o yari neyleyim\nGesi bağlarında üç top gülüm var\nHey Allah'tan korkmaz, sana bana ölüm var\nHey Allah'tan korkmaz, sana bana ölüm var\nÖlüm varsa bu dünyada zulüm var\nAtma garip anam beni dağlar ardına\nKimseler yanmasın anam yansın derdime"
            ),
            Music(
                "5",
                "Ayrılık",
                "Barış Manço",
                "baris",
                "Fikrimden geceler yatabilmirem\nBu fikri başımdan atabilmirem\nBu fikri başımdan atabilmirem\nAyrılık ayrılık aman ayrılık\nHer bir dertten ola yaman ayrılık\nUzundur hicrinden kara geceler\nBilmirem men gidem hara geceler\nBin oktur kalbime yara geceler\nAyrılık ayrılık aman ayrılık\nHer bir dertten ola yaman ayrılık"
            ),
            Music(
                "6",
                "Dağlar Dağlar",
                "Barış Manço",
                "baris2",
                "Ellerimle büyüttügüm solar iken dirilttiğim\nÇiçegimi kopardın sen, ellere verdin   x2\nDağlar dağlar\nKurban olam yol ver geçem\nSevdigimi son bir olsun yakından görem\nKuşlar ötmez, güller soldu yüce dağlar duman oldu\nBelli ki gittiğin yerden kara haber var   x2"
            ),
            Music(
                "7",
                "Unutamadım",
                "Barış Manço",
                "baris",
                "Dün yine yapayalnız dolaştım yollarda\nYağmurlarda ıslanan bomboş sokaklarda\nGözlerimde yaş kalbimde sızı unutmadım seni\nUnutamadım unutamadım ne olur anla beni\nUnutmak kolay demiştin alışırsın demiştin\nÖyleyse sen unut beni yeter ki benden isteme\nGözlerimde yaş kalbimde sızı unutmadım seni\nUnutamadım unutamadım ne olur anla beni\nYıllar ikimizden de çok şeyler götürmüş\nSen yeni yuva kurarken beni paramparça bölmüş\nGözlerimde yaş kalbimde sızı unutmadım seni\nUnutamadım unutamadım ne olur anla beni\nUnutmak kolay demiştin alışırsın demiştin\nÖyleyse sen unut beni yeter ki benden isteme"
            ),
            Music(
                "8",
                "Aynalı Kemer",
                "Barış Manço",
                "baris2",
                "Sabah yeli ılgıt ılgıt eserken\nSeher vakti bir güzele vuruldum\nAl dudakta inci dişi, bu dünyada yok bir eşi\nSeher vakti bir güzele vuruldum\nAynalı kemer ince bele bu can kurban tatlı dile\nSeher vakti bir güzele vuruldum\nMor menekşe nergiz dizmiş boynuna\nKuşluk vakti aldı beni koynuna\nCıvıldaşır dudu kuşu, sanki bülbülün ötüşü\nSeher vakti bir güzele vuruldum"
            ),
            Music(
                "9",
                "Ben Bilirim",
                "Barış Manço",
                "baris",
                "Deli gönül sevdasını, ben bilirim, ben bilirim    x2\nYardan ayrı kalmasını, ben bilirim, ben bilirim    x2\nYumuk yumuk elleri var, kömür kömür gözleri var   x2\nDaha daha neleri var, ben bilirim, ben bilirim   x2\nKışlara erdi bahar, tezkereye birkaç gün var   x2\nKışlara erdi bahar, tezkereye birkaç gün var   x2"
            ),
            Music(
                "10",
                "Nick The Chopper",
                "Barış Manço",
                "baris2",
                "Down in the forest, near a village\nLived the man called, nick the chopper\nChopping woods his game, and making money\nCaring not for trees, he blindly chops on\nNever to be married   he never washes\nHe never went to school, nick the chopper\nChopping woods his game, and getting money\nDoesn't care for life or even for a  friend now\nHe is a dirty old man, nick the chopper   x2\nNow, A very old man he had decided\nTo make his fortune, nick the chopper\nChopping woods his game, he couldn't stop it\nHe wants to cut down, all the forest\nThe trees they hated him, they made a promise\nGive him a lesson, nick the chopper\nChopping woods his game, he couldn't stop it\nBut as the story goes, he's beaten at his game\nHe is A dirty old man, nick the chopper   x2\nWe're gonna kill you, nick the chopper   x2"
            ),
        ))

        return musicList
    }
    fun loadComments(): List<Comment> {
        commentList.addAll(
            mutableListOf(
                Comment(
                    "1",
                    "4",
                    "f-arslan",
                    "Nice song",
                    "2022-05-05"
                )
            )
        )
        return commentList
    }
}