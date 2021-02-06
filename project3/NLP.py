import string
import collections

# read file and store sentences
def readData(pathName,listT):
    ferdowsiF = open(pathName,encoding="utf8")
    punctuations = ".،:؛!؟*\"\'«»"
    for line in ferdowsiF.readlines():
        line = line.translate(str.maketrans('', '', punctuations))
        line = "<s> " + line + " </s>"
        listT.append(line)


#  for finding dictionary
def sentancesTowords(ferdowsiSentencesList):
    tmp = list()
    for sentence in ferdowsiSentencesList:
        words = sentence.split()
        for word in words:
            tmp.append(word)
    return tmp

#  find frequency for each word and make a dictionary
def findFrequency(words_list):
    freq_dic = dict()
    for i in words_list:
        if not i in freq_dic:
            if words_list.count(i) >= 2:
                freq_dic.update({i:words_list.count(i)})    
    return freq_dic


def findPairsFrequency(sentences_with_start_end,uniDict):
    all_pairs = list()
    for sentence in sentences_with_start_end:
        words = sentence.split()
        for i in range(len(words) - 1):
            if words[i] in uniDict and words[i + 1] in uniDict: 
                pair = words[i] + "," + words[i + 1]
                all_pairs.append(pair)
    frequencies_pairs_dict = dict()
    for pair in all_pairs:
        if pair in frequencies_pairs_dict:                   # If this pair is already exist in the dict: increase the value by +1
            newFrequency = frequencies_pairs_dict[pair] + 1
            frequencies_pairs_dict.update({pair: newFrequency})
        else:                                           # If this pair is not already exist in the dict: create and make its value 1
            frequencies_pairs_dict.update({pair: 1})
    return frequencies_pairs_dict


# generate unigram model
def buildUnigram(uniDict):
    uniModel = dict()
    total = sum(uniDict.values())
    for word, count in uniDict.items():
        uniModel.update({word: count / total})
    return uniModel


# generated bigram model
def buildBigram(biDict,uniDict):
    bigram_model = dict()
    for pair, count in biDict.items():
        tmp_list = pair.split(",")
        bigram_model.update({pair: count / uniDict.get(tmp_list[0])})
    return bigram_model



def backOffModel(pair,bigram_model,uni_model,l3,l2,l1,e):
    if pair in bigram_model:
        probb = bigram_model[pair]
    else:
        probb = 0
    tmp = (pair.split(","))[1]
    if tmp in uni_model:
        probu = uni_model[tmp]
    else:
        probu = 0
    return l3*probb + l2*probu + l1*e

def findPoet(poem,ferdowsiUni,fedowsiBi,hafezUni,hafezBi,molaviUni,molaviBi):
    ferdowsi = 1
    hafez = 1
    molavi = 1
    words = poem.split(" ")
    l1 = 0.05
    l2 = 0.85
    l3 = 0.1
    e = 0.0001
    for i in range(len(words) - 1):
        pair = words[i] + "," + words[i + 1]
        ferdowsi *= backOffModel(pair,fedowsiBi,ferdowsiUni,l3,l2,l1,e)
        hafez *= backOffModel(pair,hafezBi,hafezUni,l3,l2,l1,e)
        molavi *= backOffModel(pair,molaviBi,molaviUni,l3,l2,l1,e)
    if ferdowsi>hafez and ferdowsi>molavi:
        return 1
    elif hafez>ferdowsi and hafez>molavi:
        return 2
    else:
        return 3

if __name__ == "__main__":
    ferdowsiSentencesList = list()
    hafezSentencesList = list()
    molaviSentencesList = list()

    # reading files
    readData("./AI_P3/train_set/ferdowsi_train.txt",ferdowsiSentencesList)
    readData("./AI_P3/train_set/hafez_train.txt",hafezSentencesList)
    readData("./AI_P3/train_set/molavi_train.txt",molaviSentencesList)
    #getwords
    ferdowsi_word = sentancesTowords(ferdowsiSentencesList)
    hafez_word = sentancesTowords(hafezSentencesList)
    molavi_word = sentancesTowords(molaviSentencesList)


    # unigram - bigram
    uniDicFerdowsi = findFrequency(ferdowsi_word)
    biDicFerdowsi = findPairsFrequency(ferdowsiSentencesList,uniDicFerdowsi)

    uniDicHafez = findFrequency(hafez_word)
    biDicHafez = findPairsFrequency(hafezSentencesList,uniDicHafez)

    uniDicMolavi = findFrequency(molavi_word)
    biDicMolavi  = findPairsFrequency(molaviSentencesList,uniDicMolavi)

    # unigram model - bigram model
    ferdowsi_uni_model = buildUnigram(uniDicFerdowsi)
    ferdowsi_bigram_model = buildBigram(biDicFerdowsi,uniDicFerdowsi)

    hafez_uni_model = buildUnigram(uniDicHafez)
    hafez_bigram_model = buildBigram(biDicHafez,uniDicHafez)

    molavi_uni_model = buildUnigram(uniDicMolavi)
    molavi_bigram_model = buildBigram(biDicMolavi,uniDicMolavi)

    poems = open("./AI_P3/test_set/test_file.txt",encoding="utf8")
    count  = 0 
    total = 0
    for line in poems.readlines():
        poem = line.split("\t")
        poet = int(poem[0])
        re = findPoet(poem[1],ferdowsi_uni_model,ferdowsi_bigram_model,hafez_uni_model,hafez_bigram_model,molavi_uni_model,molavi_bigram_model)
        if re == poet :
            count+=1
        total+=1
    print(count / total)
        
        

    


    