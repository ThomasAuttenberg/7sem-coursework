export const getPhoneMask = () => ({
  mask:'+{7}(000)000-00-00',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  prepare: (appended:string, masked:any) => {
    if (appended === '8' && masked.value === '') {
      return '+7';
    }
    return appended;
  },
});

export const getCISCityMask = () => ({
  mask: /^[А-ЯЁа-яё\s-]+$/
});
export const getIntegerMask = () => ({
  mask: /^[1-9]([0-9]+)?$/,
});
export const getLatinMask = () => ({
  mask: /^[A-Za-z\s-]+$/
});

export const getLoginMask = () => ({
  mask: /^[A-Za-z0-9\s-]+$/
})

export const getTNCodeMask = () => ({
  mask: /^\d{0,10}$/
})
export const getNumberMask  =  (scale:number = 3, maxIntegerPartDigits: number = 9) => {
  const regex = new RegExp(`^\\d{1,${maxIntegerPartDigits}}(\\.(\\d{1,${scale}})?)?$`);
  return {
    mask: regex,
    prepare: (appended:string) => {
      const endIndex = appended.length - 1;
      if (appended[endIndex] === ',') {
        appended = appended.slice(0,endIndex) + '.';
      }
      return appended;
    }
  }
};
