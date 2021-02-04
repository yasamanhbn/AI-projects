import string
import collections

ferdowsiDic = set()
ferdowsiSentencesList = list()

# read file and store sentences
def readData(pathName,listT):
    ferdowsiF = open(pathName,encoding="utf8")
    punctuations = ".،:؛!؟*\"\'«»"
    for line in ferdowsiF.readlines():
        line = line.translate(str.maketrans('', '', punctuations))
        listT.append(line)


#  for adding <s> and </s> to the sentences
def sentenceStartEnd(sentences_list):
    temp_list = list()
    for sentence in sentences_list:
        sentence = "<s> " + sentence + " </s>"
        temp_list.append(sentence)
    return temp_list


#  for finding dictionary
def sentancesTowords():
    tmp = list()
    for sentence in ferdowsiSentencesList:
        words = sentence.split()
        for word in words:
            tmp.append(word)
    return tmp

#  find frequency for each word and make a dictionary
def findFrequency(words_list,ferdowsiDic):
    freq_dic = dict()
    for i in words_list:
        if not i in freq_dic:
            if words_list.count(i)>2:
                ferdowsiDic.add(i)  
                freq_dic.update({i:words_list.count(i)})
    return freq_dic


def findPairsFrequency(sentences_with_start_end):
    all_pairs = list()
    for sentence in sentences_with_start_end:
        words = sentence.split()
        pairs = zip(*[words[i:] for i in range(2)])     # creating pairs for Ngram
        all_pairs.append([" ".join(pair) for pair in pairs])
    frequencies_pairs_dict = dict()
    for pair in all_pairs:
        for pai in pair:
            if (pai in frequencies_pairs_dict):                   # If this pair is already exist in the dict: increase the value by +1
                newFrequency = frequencies_pairs_dict[pai] + 1
                frequencies_pairs_dict.update({pai: newFrequency})
            else:                                           # If this pair is not already exist in the dict: create and make its value 1
                frequencies_pairs_dict.update({pai: 1})
    temp_list = frequencies_pairs_dict.copy()
    for pair in temp_list:
        if frequencies_pairs_dict.get(pair)<=2:
            frequencies_pairs_dict.pop(pair)
    return frequencies_pairs_dict


# generate unigram model and return an orderd dictionary
def buildUnigram(uniDict):
    uniModel = dict()
    total = sum(uniDict.values())
    for word, count in uniDict.items():
        uniModel.update({word: count / total})
    sorted_dict = sorted(uniModel.items(), key=lambda kv: kv[1])
    sorted_unigram_model = collections.OrderedDict(sorted_dict)
    return sorted_unigram_model


# generated bigram model and return an ordered dictionary
def buildBigram(biDict,uniDict):
    bigram_model = dict()
    for pair, count in biDict.items():
        bigram_model.update({pair: count / uniDic[pair[0]]})
    sorted_dict = sorted(bigram_model.items(), key=lambda kv: kv[1])
    sorted_bigram_model = collections.OrderedDict(sorted_dict)
    return sorted_bigram_model


if __name__ == "__main__":
    readData("./AI_P3/train_set/ferdowsi_train.txt",ferdowsiSentencesList)
    tmp = sentancesTowords()
    uniDic = findFrequency(tmp,ferdowsiDic)
    sentences_with_start_end = sentenceStartEnd(ferdowsiSentencesList)
    biDic = findPairsFrequency(sentences_with_start_end)





    